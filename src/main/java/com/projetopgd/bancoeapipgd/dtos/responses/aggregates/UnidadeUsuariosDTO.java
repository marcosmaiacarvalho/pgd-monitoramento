package com.projetopgd.bancoeapipgd.dtos.responses.aggregates;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UnidadeUsuariosDTO {

    private String siglaUnidade;
    private String siglaUnidadeParaHtml;  //Com o caractere "/" substituído para "_".
    private List<UsuarioPlanosDTO> usuariosComPlanos;

}
