package com.projetopgd.bancoeapipgd.utils;

import java.time.LocalDate;
import java.time.YearMonth;

public final class DataUtils {

    private static final YearMonth MES_ANO_LIMITE_INICIO = YearMonth.of(2024, 6);

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

    public static YearMonth getMesAnoLimiteInicio() {
        return MES_ANO_LIMITE_INICIO;
    }

    public static YearMonth converterDataParaMesAno(LocalDate data) {
        return YearMonth.of(data.getYear(), data.getMonth());
    }


}
