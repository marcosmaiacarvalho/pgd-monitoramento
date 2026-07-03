package com.projetopgd.bancoeapipgd.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum StatusExecucao {
    AFASTAMENTO_INTEGRAL("AFASTAMENTO INTEGRAL"),
    META_CUMPRIDA_EXATA("ENTREGUE INTEGRALMENTE"),
    DEVENDO_HORAS("PENDENTE DE %.1f HORA(S)"),
    HORAS_EXCEDENTES("ENTREGUE COM %.1f EXCEDENTES");

    private final String templateMensagem;

    public String getMensagemFormatada(Double saldoDeHoras) {
        if (saldoDeHoras == null || saldoDeHoras == 0.0) {
            return this.templateMensagem;
        }
        return String.format(this.templateMensagem, Math.abs(saldoDeHoras));
    }
}
