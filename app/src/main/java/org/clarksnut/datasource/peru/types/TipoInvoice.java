package org.clarksnut.datasource.peru.types;

import java.util.Optional;
import java.util.stream.Stream;

public enum TipoInvoice {

    FACTURA("01", "FACTURA"),
    BOLETA("03", "BOLETA");

    private final String codigo;
    private final String denominacion;

    public String getCodigo() {
        return codigo;
    }

    public String getDenominacion() {
        return denominacion;
    }

    TipoInvoice(String codigo, String denominacion) {
        this.codigo = codigo;
        this.denominacion = denominacion;
    }

    public static Optional<TipoInvoice> getFromCode(String codigo) {
        return Stream.of(TipoInvoice.values())
                .filter(p -> p.getCodigo().equals(codigo))
                .findFirst();
    }

}
