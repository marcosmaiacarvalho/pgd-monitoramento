package com.projetopgd.bancoeapipgd.dtos.responses.base;

import lombok.Data;

import java.time.LocalDate;
import java.time.YearMonth;

@Data
public class PlanoDTO {

    private LocalDate inicioVigencia;
    private LocalDate fimVigencia;
    private YearMonth vigencia;
    private String siglaUnidadePlano;
    private Double metaPlano;
    private Double tempoEntregue;
    private Double percentualEntregue;
    private String statusExecucao;
    private Boolean statusAvaliacao;
    private Boolean statusHomologacao;

}
