package com.zeloconsultas.shared.util;

import java.time.LocalDate;

// Formata data como "18 de Junho de 2024" (mes capitalizado, no padrao do
// atestado). Nomes dos meses fixos em portugues pra nao depender de Locale/CLDR
// da JVM onde o app roda (o notebook da medica).
public final class DataPorExtenso {

    private static final String[] MESES = {
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    };

    private DataPorExtenso() {
    }

    public static String formatar(LocalDate data) {
        return data.getDayOfMonth() + " de " + MESES[data.getMonthValue() - 1] + " de " + data.getYear();
    }
}
