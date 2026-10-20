package com.projetopgd.bancoeapipgd.utils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public final class DataUtils {

    private static final YearMonth MES_ANO_LIMITE_INICIO = YearMonth.of(2024, 6);
    private static final DateTimeFormatter FORMATO_MES_ANO = DateTimeFormatter.ofPattern("MM/yyyy");

    private DataUtils() {
    }


    public static LocalDate getDataLimiteDeInicio() {
        return MES_ANO_LIMITE_INICIO.atDay(1);
    }

    public static LocalDate getDataLimiteFim() {
        return YearMonth.now().minusMonths(1).atEndOfMonth();
    }

    public static LocalDate converterMesAnoParaInicioDoMesOuBuscarPadrao(YearMonth mesAno) {
        if (mesAno == null) {
            return getDataLimiteDeInicio();
        }

        return mesAno.atDay(1);
    }

    public static LocalDate converterMesAnoParaFimDoMesOuBuscarPadrao(YearMonth mesAno) {

        if (mesAno == null) {
            return getDataLimiteFim();
        }

        return mesAno.atEndOfMonth();
    }


    public static YearMonth getMesAnoLimiteFim() {
        return YearMonth.now().minusMonths(1);
    }

    // Corte do PRAZO DE AVALIAÇÃO: mês atual - 2.
    // Planos dos últimos 2 meses ainda estão dentro do prazo da chefia avaliar,
    // então não contam como pendência de avaliação ainda.
    public static YearMonth getMesAnoLimitePrazoAvaliacao() {
        return YearMonth.now().minusMonths(2);
    }

    public static LocalDate getDataLimitePrazoAvaliacao() {
        return YearMonth.now().minusMonths(2).atEndOfMonth();
    }

    public static LocalDate converterMesAnoParaFimDoMesOuBuscarPrazoAvaliacao(YearMonth mesAno) {
        if (mesAno == null) {
            return getDataLimitePrazoAvaliacao();
        }
        return mesAno.atEndOfMonth();
    }

    public static YearMonth getMesAnoLimiteInicio() {
        return MES_ANO_LIMITE_INICIO;
    }

    public static YearMonth converterDataParaMesAno(LocalDate data) {
        return YearMonth.of(data.getYear(), data.getMonth());
    }

    // Formata um mês/ano para exibição no front (ex.: "06/2026").
    public static String formatarMesAno(YearMonth mesAno) {
        return mesAno.format(FORMATO_MES_ANO);
    }

}
