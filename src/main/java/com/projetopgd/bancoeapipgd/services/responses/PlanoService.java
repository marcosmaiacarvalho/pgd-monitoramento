package com.projetopgd.bancoeapipgd.services.responses;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.projetopgd.bancoeapipgd.dtos.reports.PlanoCsvDTO;
import com.projetopgd.bancoeapipgd.dtos.responses.aggregates.UnidadeUsuariosDTO;
import com.projetopgd.bancoeapipgd.dtos.responses.aggregates.UsuarioUnidadesDTO;
import com.projetopgd.bancoeapipgd.entities.Plano;
import com.projetopgd.bancoeapipgd.entities.Usuario;
import com.projetopgd.bancoeapipgd.mappers.PlanoMapper;
import com.projetopgd.bancoeapipgd.repositories.PlanoRepository;
import com.projetopgd.bancoeapipgd.utils.DataUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PlanoService {

    private final UsuarioService usuarioService;
    private final PlanoRepository planoRepository;
    private final PlanoMapper planoMapper;
    private final PlanoAgregador planoAgregador;

    // ========================================================================
    // 1. MÉTODOS PÚBLICOS (Pontos de Entrada / APIs)
    // ========================================================================

    @Transactional(readOnly = true)
    public List<UsuarioUnidadesDTO> buscarPlanosPorServidor(String matricula) {

        Usuario usuario = usuarioService.buscarEntidadePorMatricula(matricula);
        List<Plano> planos = planoRepository.findByUsuarioIdOrderByInicioVigenciaDesc(usuario.getId());

        Map<Usuario, Map<String, List<Plano>>> planosPorServidor = planoAgregador.agruparPorServidorUnidade(planos);
        return planoAgregador.montarListaDeUsuarios(planosPorServidor);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioUnidadesDTO> buscarPlanosIncompletosPaginados(YearMonth inicioVigencia, YearMonth fimVigencia, Pageable pageable) {

        LocalDate dataInicioVigencia = DataUtils.converterMesAnoParaInicioDoMesOuBuscarPadrao(inicioVigencia);
        LocalDate dataFimVigencia = DataUtils.converterMesAnoParaFimDoMesOuBuscarPadrao(fimVigencia);

        Page<Long> idsUsuariosComPlanosIncompletos = planoRepository.buscarIdsUsuariosPaginadosPorPlanosIncompletos(dataInicioVigencia, dataFimVigencia, pageable);
        List<Plano> planos = planoRepository.buscarPlanosIncompletosPorIdsUsuarios(dataInicioVigencia, dataFimVigencia, idsUsuariosComPlanosIncompletos.getContent());

        Map<Usuario, Map<String, List<Plano>>> planosPorServidor = planoAgregador.agruparPorServidorUnidade(planos);
        List<UsuarioUnidadesDTO> usuariosUnidades = planoAgregador.montarListaDeUsuarios(planosPorServidor);

        return new PageImpl<>(usuariosUnidades, pageable, idsUsuariosComPlanosIncompletos.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<UnidadeUsuariosDTO> buscarPlanosNaoAvaliadosPaginados(YearMonth inicioVigencia, YearMonth fimVigencia, Pageable pageable) {

        LocalDate dataInicioVigencia = DataUtils.converterMesAnoParaInicioDoMesOuBuscarPadrao(inicioVigencia);
        LocalDate dataFimVigencia = DataUtils.converterMesAnoParaFimDoMesOuBuscarPrazoAvaliacao(fimVigencia);

        Page<String> siglasUnidades = planoRepository.buscarSiglasUnidadesPaginadasPorPlanosNaoAvaliados(dataInicioVigencia, dataFimVigencia, pageable);
        List<Plano> planos = planoRepository.buscarPlanosNaoAvaliadosPorSiglasUnidades(dataInicioVigencia, dataFimVigencia, siglasUnidades.getContent());

        Map<String, Map<Usuario, List<Plano>>> servidoresPorUnidade = planoAgregador.agruparUnidadeComUsuarios(planos);
        List<UnidadeUsuariosDTO> unidadeComUsuariosDTO = planoAgregador.montarListaDeUnidadesComUsuarios(servidoresPorUnidade);

        return new PageImpl<>(unidadeComUsuariosDTO, pageable, siglasUnidades.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<UsuarioUnidadesDTO> buscarPlanosPorPeriodoPaginados(YearMonth inicioVigencia, YearMonth fimVigencia, Pageable pageable) {

        LocalDate dataInicioVigencia = DataUtils.converterMesAnoParaInicioDoMesOuBuscarPadrao(inicioVigencia);
        LocalDate dataFimVigencia = DataUtils.converterMesAnoParaFimDoMesOuBuscarPadrao(fimVigencia);

        Page<Long> idsUsuarios = planoRepository.buscarIdsUsuariosPaginadosPorPeriodo(dataInicioVigencia, dataFimVigencia, pageable);
        List<Plano> planos = planoRepository.buscarTodosOsPlanosPorIdsUsuarios(dataInicioVigencia, dataFimVigencia, idsUsuarios.getContent());

        Map<Usuario, Map<String, List<Plano>>> planosPorServidor = planoAgregador.agruparPorServidorUnidade(planos);
        List<UsuarioUnidadesDTO> usuariosUnidades = planoAgregador.montarListaDeUsuarios(planosPorServidor);

        return new PageImpl<>(usuariosUnidades, pageable, idsUsuarios.getTotalElements());
    }

    @Transactional(readOnly = true)
    public void exportarNaoAvaliadosParaCsv(YearMonth inicioVigencia, YearMonth fimVigencia, PrintWriter writer) {
        List<Plano> planos = buscarPlanosCompletosNaoAvaliados(inicioVigencia, fimVigencia);
        converterParaCsv(planos, writer);
    }

    @Transactional(readOnly = true)
    public void exportarIncompletosParaCsv(YearMonth inicioVigencia, YearMonth fimVigencia, PrintWriter writer) {
        List<Plano> planos = buscarPlanosIncompletosNaoAvaliados(inicioVigencia, fimVigencia);
        converterParaCsv(planos, writer);
    }

    // ========================================================================
    // 2. CONSULTAS AO BANCO COMPARTILHADAS
    // ========================================================================

    protected List<Plano> buscarPlanosCompletosNaoAvaliados(YearMonth inicioVigencia, YearMonth fimVigencia) {
        LocalDate dataInicioVigencia = DataUtils.converterMesAnoParaInicioDoMesOuBuscarPadrao(inicioVigencia);
        LocalDate dataFimVigencia = DataUtils.converterMesAnoParaFimDoMesOuBuscarPrazoAvaliacao(fimVigencia);

        return planoRepository.buscarPorVigenciaCompletosNaoAvaliados(dataInicioVigencia, dataFimVigencia);
    }

    protected List<Plano> buscarPlanosIncompletosNaoAvaliados(YearMonth inicioVigencia, YearMonth fimVigencia) {
        LocalDate dataInicioVigencia = DataUtils.converterMesAnoParaInicioDoMesOuBuscarPadrao(inicioVigencia);
        LocalDate dataFimVigencia = DataUtils.converterMesAnoParaFimDoMesOuBuscarPadrao(fimVigencia);

        return planoRepository.buscarPorVigenciaIncompletosNaoAvaliados(dataInicioVigencia, dataFimVigencia);
    }

    protected List<Plano> buscarTodosPlanosPorVigencia(YearMonth inicioVigencia, YearMonth fimVigencia) {
        LocalDate dataInicioVigencia = DataUtils.converterMesAnoParaInicioDoMesOuBuscarPadrao(inicioVigencia);
        LocalDate dataFimVigencia = DataUtils.converterMesAnoParaFimDoMesOuBuscarPadrao(fimVigencia);

        return planoRepository.buscarPlanosPorVigencia(dataInicioVigencia, dataFimVigencia);
    }

    protected List<Plano> buscarPlanosNaoAvaliados(YearMonth inicioVigencia, YearMonth fimVigencia) {
        LocalDate dataInicioVigencia = DataUtils.converterMesAnoParaInicioDoMesOuBuscarPadrao(inicioVigencia);
        LocalDate dataFimVigencia = DataUtils.converterMesAnoParaFimDoMesOuBuscarPadrao(fimVigencia);

        return planoRepository.buscarPorVigenciaTodosNaoAvaliados(dataInicioVigencia, dataFimVigencia);
    }


    // ========================================================================
    // 3. EXPORTAÇÃO EM CSV
    // ========================================================================

    private void converterParaCsv(List<Plano> planos, PrintWriter writer) {

        List<PlanoCsvDTO> linhasCsv = planos.stream().map(planoMapper::entidadeParaCsvDTO).toList();

        ColumnPositionMappingStrategy<PlanoCsvDTO> ordenacaoDasColunasCsv = new ColumnPositionMappingStrategy<>();
        ordenacaoDasColunasCsv.setType(PlanoCsvDTO.class);

        StatefulBeanToCsv<PlanoCsvDTO> beanToCsv = new StatefulBeanToCsvBuilder<PlanoCsvDTO>(writer)
                .withSeparator(';')
                .withMappingStrategy(ordenacaoDasColunasCsv)
                .build();

        writer.println("SIAPE;SERVIDOR;EMAIL;UNIDADE DO PLANO;VIGÊNCIA;INÍCIO DO PLANO;FIM DO PLANO;% DA EXECUÇÃO;META DO PLANO;TEMPO ENTREGUE;AVALIADO;HOMOLOGADO");

        try {
            beanToCsv.write(linhasCsv);
        } catch (
                Exception e) { //Generalizo as Exceptions para lançar uma Runtime (provisório até criar handlers globais)
            throw new RuntimeException("Erro na escrita do CSV");
        }
    }

    public Integer buscarQuantidadeDePlanos(YearMonth inicioVigencia, YearMonth fimVigencia) {

        LocalDate dataInicioVigencia = DataUtils.converterMesAnoParaInicioDoMesOuBuscarPadrao(inicioVigencia);
        LocalDate dataFimVigencia = DataUtils.converterMesAnoParaFimDoMesOuBuscarPadrao(fimVigencia);

        return planoRepository.buscarQuantidadeDePlanos(dataInicioVigencia, dataFimVigencia);

    }
}