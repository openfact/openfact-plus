package org.clarksnut.datasource.peru.types;

import java.util.Optional;
import java.util.stream.Stream;

public enum TipoPagoResumen {

    TOTAL_VENTA_OPERACIONES_GRAVADAS("01"),
    TOTAL_VENTA_OPERACIONES_EXONERADAS("02"),
    TOTAL_VENTA_OPERACIONES_INAFECTAS("03");

    private String codigo;

    public String getCodigo() {
        return codigo;
    }

    TipoPagoResumen(String codigo) {
        this.codigo = codigo;
    }

    public static Optional<TipoPagoResumen> getFromCode(String codigo) {
        return Stream.of(TipoPagoResumen.values())
                .filter(p -> p.getCodigo().equals(codigo))
                .findFirst();
    }
}
