package com.projetopgd.bancoeapipgd.services.responses;


import com.projetopgd.bancoeapipgd.dtos.responses.aggregates.UnidadePlanosDTO;
import com.projetopgd.bancoeapipgd.dtos.responses.aggregates.UnidadeUsuariosDTO;
import com.projetopgd.bancoeapipgd.dtos.responses.aggregates.UsuarioPlanosDTO;
import com.projetopgd.bancoeapipgd.dtos.responses.aggregates.UsuarioUnidadesDTO;
import com.projetopgd.bancoeapipgd.dtos.responses.base.PlanoDTO;
import com.projetopgd.bancoeapipgd.dtos.responses.base.UsuarioDTO;
import com.projetopgd.bancoeapipgd.entities.Plano;
import com.projetopgd.bancoeapipgd.entities.Usuario;
import com.projetopgd.bancoeapipgd.mappers.PlanoMapper;
import com.projetopgd.bancoeapipgd.mappers.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PlanoAgregador {

    private final UsuarioMapper usuarioMapper;
    private final PlanoMapper planoMapper;

    // ========================================================================
    // 1. AGRUPAMENTO POR SERVIDOR -> UNIDADES -> PLANOS
    // ========================================================================

    public Map<Usuario, Map<String, List<Plano>>> agruparPorServidorUnidade(List<Plano> planos) {
        return planos.stream().collect(
                Collectors.groupingBy(Plano::getUsuario,
                        Collectors.groupingBy(Plano::getSiglaUnidade))
        );
    }

    public List<UsuarioUnidadesDTO> montarListaDeUsuarios(Map<Usuario, Map<String, List<Plano>>> planosPorServidor) {
        return planosPorServidor.entrySet().stream().map(this::montarUsuarioUnidades)
                .sorted(Comparator.comparing(usuarioUnidades -> usuarioUnidades.getUsuario().getNome()))
                .toList();
    }

    public UsuarioUnidadesDTO montarUsuarioUnidades(Map.Entry<Usuario, Map<String, List<Plano>>> usuarioUnidades) {
        UsuarioDTO usuarioDTO = usuarioMapper.entidadeParaDTO(usuarioUnidades.getKey());
        List<UnidadePlanosDTO> unidadePlanos = montarListaDeUnidades(usuarioUnidades.getValue());

        return new UsuarioUnidadesDTO(usuarioDTO, unidadePlanos);
    }

    public List<UnidadePlanosDTO> montarListaDeUnidades(Map<String, List<Plano>> unidadePlanos) {
        return unidadePlanos.entrySet().stream().map(this::montarUnidadePlanos)
                .sorted(Comparator.comparing(unidadeComPlanos -> unidadeComPlanos.getPlanos().get(0).getInicioVigencia(), //get(0) pois já está ordenado os planos por ordem decresc
                        Comparator.reverseOrder()))
                .toList();
    }

    public UnidadePlanosDTO montarUnidadePlanos(Map.Entry<String, List<Plano>> unidadePlanos) {
        String siglaUnidade = unidadePlanos.getKey();
        String siglaUnidadeParaHtml = siglaUnidade.replace("/", "_");
        List<PlanoDTO> planosDoServidorNaUnidade = mapearListaParaResponseDTO(unidadePlanos.getValue());

        return new UnidadePlanosDTO(siglaUnidade, siglaUnidadeParaHtml, planosDoServidorNaUnidade);
    }

    // ========================================================================
    // 2. AGRUPAMENTO POR UNIDADE -> SERVIDORES -> PLANOS
    // ========================================================================

    public Map<String, Map<Usuario, List<Plano>>> agruparUnidadeComUsuarios(List<Plano> planos) {
        return planos.stream().collect(Collectors.groupingBy(Plano::getSiglaUnidade,
                Collectors.groupingBy(Plano::getUsuario)));
    }

    public List<UnidadeUsuariosDTO> montarListaDeUnidadesComUsuarios(Map<String, Map<Usuario, List<Plano>>> servidoresPorUnidade) {
        return servidoresPorUnidade.entrySet().stream().map(this::montarUnidadeComUsuarios)
                .sorted(Comparator.comparing(UnidadeUsuariosDTO::getSiglaUnidade)).toList();
    }

    public UnidadeUsuariosDTO montarUnidadeComUsuarios(Map.Entry<String, Map<Usuario, List<Plano>>> unidadeComUsuarios) {

        String siglaUnidade = unidadeComUsuarios.getKey();
        String siglaUnidadeParaHtml = siglaUnidade.replace("/", "_");
        List<UsuarioPlanosDTO> listaDeUsuariosNaUnidadeComSeusPlanos = unidadeComUsuarios.getValue().entrySet().stream()
                .map(this::montarUsuarioComPlanos).sorted(Comparator.comparing(
                        usuarioPlanos -> usuarioPlanos.getUsuario().getNome())).toList();

        return new UnidadeUsuariosDTO(siglaUnidade, siglaUnidadeParaHtml, listaDeUsuariosNaUnidadeComSeusPlanos);
    }

    public UsuarioPlanosDTO montarUsuarioComPlanos(Map.Entry<Usuario, List<Plano>> usuarioComPlanos) {

        UsuarioDTO usuarioDTO = usuarioMapper.entidadeParaDTO(usuarioComPlanos.getKey());
        List<PlanoDTO> listaDePlanos = mapearListaParaResponseDTO(usuarioComPlanos.getValue());

        return new UsuarioPlanosDTO(usuarioDTO, listaDePlanos);
    }

    // ========================================================================
    // 3. MAPEAMENTO E CONVERSÃO DTO (WEB)
    // ========================================================================

    public List<PlanoDTO> mapearListaParaResponseDTO(List<Plano> planos) {
        return planos.stream().map(planoMapper::entidadeParaPaginaDTO)
                .sorted(Comparator.comparing(PlanoDTO::getInicioVigencia, Comparator.reverseOrder()))
                .toList();
    }
}
