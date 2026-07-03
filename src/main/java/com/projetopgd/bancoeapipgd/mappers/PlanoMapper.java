package com.projetopgd.bancoeapipgd.mappers;

import com.projetopgd.bancoeapipgd.dtos.reports.PlanoCsvDTO;
import com.projetopgd.bancoeapipgd.dtos.responses.base.PlanoDTO;
import com.projetopgd.bancoeapipgd.entities.Plano;
import com.projetopgd.bancoeapipgd.utils.DataUtils;
import org.springframework.stereotype.Component;

@Component
public class PlanoMapper {


    public PlanoDTO entidadeParaPaginaDTO(Plano entidade) {

        if (entidade == null) {
            return null;
        }

        PlanoDTO dto = new PlanoDTO();

        dto.setInicioVigencia(entidade.getInicioVigencia());
        dto.setFimVigencia(entidade.getFimVigencia());
        dto.setVigencia(DataUtils.converterDataParaMesAno(entidade.getInicioVigencia()));
        dto.setSiglaUnidadePlano(entidade.getSiglaUnidade());
        dto.setMetaPlano(entidade.getTempoProporcional());
        dto.setTempoEntregue(entidade.getTempoEntregue());
        dto.setPercentualEntregue(entidade.calcularPercentualEntregue());

        Double saldoDeHoras = entidade.calcularSaldoDeHoras();

        dto.setStatusExecucao(entidade.verificarStatusExecucao().getMensagemFormatada(saldoDeHoras));
        dto.setStatusAvaliacao(entidade.getEstaAvaliado());
        dto.setStatusHomologacao(entidade.getEstaHomologado());

        return dto;
    }

    public PlanoCsvDTO entidadeParaCsvDTO(Plano entidade) {
        if (entidade == null) {
            return null;
        }

        PlanoCsvDTO dto = new PlanoCsvDTO();

        dto.setMatriculaUsuario(entidade.getUsuario().getMatricula());
        dto.setNomeUsuario(entidade.getUsuario().getNome());
        dto.setEmail(entidade.getUsuario().getEmail());

        dto.setSiglaUnidadePlano(entidade.getSiglaUnidade());
        dto.setVigencia(DataUtils.converterDataParaMesAno(entidade.getInicioVigencia()));
        dto.setInicioVigencia(entidade.getInicioVigencia());
        dto.setFimVigencia(entidade.getFimVigencia());
        dto.setPercentualEntregue(entidade.calcularPercentualEntregue());
        dto.setMetaPlano(entidade.getTempoProporcional());
        dto.setTempoEntregue(entidade.getTempoEntregue());
        dto.setStatusAvaliacao(booleanParaTexto(entidade.getEstaAvaliado()));
        dto.setStatusHomologacao(booleanParaTexto(entidade.getEstaHomologado()));

        return dto;
    }

    private String booleanParaTexto(Boolean arg) {
        return arg ? "SIM" : "NÃO";
    }
}
