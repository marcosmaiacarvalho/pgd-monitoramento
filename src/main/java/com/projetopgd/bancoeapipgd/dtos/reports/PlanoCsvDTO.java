package com.projetopgd.bancoeapipgd.dtos.reports;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import lombok.Data;

import java.time.LocalDate;
import java.time.YearMonth;

@Data
public class PlanoCsvDTO {

    @CsvBindByPosition(position = 0)
    private String matriculaUsuario;

    @CsvBindByPosition(position = 1)
    private String nomeUsuario;

    @CsvBindByPosition(position = 2)
    private String email;

    @CsvBindByPosition(position = 3)
    private String siglaUnidadePlano;

    @CsvBindByPosition(position = 4)
    @CsvDate("MM/yyyy")
    private YearMonth vigencia;

    @CsvBindByPosition(position = 5)
    @CsvDate("dd/MM/yyyy")
    private LocalDate inicioVigencia;

    @CsvBindByPosition(position = 6)
    @CsvDate("dd/MM/yyyy")
    private LocalDate fimVigencia;

    @CsvBindByPosition(position = 7)
    private Double percentualEntregue;

    @CsvBindByPosition(position = 8)
    private Double metaPlano;

    @CsvBindByPosition(position = 9)
    private Double tempoEntregue;

    @CsvBindByPosition(position = 10)
    private String statusAvaliacao;

    @CsvBindByPosition(position = 11)
    private String statusHomologacao;

}
