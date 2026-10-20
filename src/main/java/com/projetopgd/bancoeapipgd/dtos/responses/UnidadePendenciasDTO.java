package com.projetopgd.bancoeapipgd.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnidadePendenciasDTO {

    private String siglaUnidade;
    private Long qtdPendencia;


}
