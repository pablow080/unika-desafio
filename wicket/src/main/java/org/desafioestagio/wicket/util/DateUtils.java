package org.desafioestagio.wicket.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter EXIBICAO_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static String formatarParaExibicao(LocalDate data) {
        return data != null ? data.format(EXIBICAO_FORMATTER) : "";
    }

    public static LocalDate parsearDeExibicao(String dataTexto) {
        return dataTexto != null && !dataTexto.isEmpty() ? LocalDate.parse(dataTexto, EXIBICAO_FORMATTER) : null;
    }

    public static String formatarParaApi(LocalDate data) {
        return data != null ? data.format(ISO_FORMATTER) : "";
    }

    public static LocalDate parsearDaApi(String dataTexto) {
        return dataTexto != null && !dataTexto.isEmpty() ? LocalDate.parse(dataTexto, ISO_FORMATTER) : null;
    }
}
