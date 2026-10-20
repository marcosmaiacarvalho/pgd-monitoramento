package com.projetopgd.bancoeapipgd.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResumoPendenciasDTO {

    private Integer qtdTotal;
    private Integer qtdPendenciaChefia;
    private Integer qtdPendenciaServidor;
    private String rotuloPeriodo; //período que este resumo representa, ex.: "07/2026" ou "06/2024 até 06/2026"

    //Taxa de adimplência = % de planos SEM pendência (avaliados) sobre o total.
    //Calculado aqui no backend (e não no template) pra manter a regra de negócio no lugar certo.
    public Double getTaxaAdimplencia() {
        if (qtdTotal == null || qtdTotal == 0) {
            return 0.0;
        }
        int qtdSemPendencia = qtdTotal - qtdPendenciaChefia - qtdPendenciaServidor;
        return Math.floor((double) qtdSemPendencia / qtdTotal * 1000) / 10; //1 casa decimal, arredondando pra baixo
    }
}
