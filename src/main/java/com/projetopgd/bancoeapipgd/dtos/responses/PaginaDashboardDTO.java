package com.projetopgd.bancoeapipgd.dtos.responses;

import lombok.Data;

import java.util.List;

@Data
public class PaginaDashboardDTO {

    private ResumoPendenciasDTO resumoMesPassado;      // mês atual - 1
    private ResumoPendenciasDTO resumoMesRetrasado;    // mês atual - 2
    private ResumoPendenciasDTO resumoPeriodoCompleto; // total geral
    private List<UnidadePendenciasDTO> unidadesComPendenciaChefia;
    private List<UnidadePendenciasDTO> unidadesComPendenciaServidor;


}
