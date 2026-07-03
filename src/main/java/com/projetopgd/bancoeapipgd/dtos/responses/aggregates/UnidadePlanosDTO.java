package com.projetopgd.bancoeapipgd.dtos.responses.aggregates;

import com.projetopgd.bancoeapipgd.dtos.responses.base.PlanoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UnidadePlanosDTO {

    private String siglaUnidade;
    private String siglaUnidadeParaHtml;  //Com o caractere "/" substituído para "_".
    private List<PlanoDTO> planos; //Meu agrupamento no Service já garante que aqui só vão ter planos do mesmo servidor


}
