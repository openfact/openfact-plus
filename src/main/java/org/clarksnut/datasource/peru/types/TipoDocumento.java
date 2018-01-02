package org.clarksnut.datasource.peru.types;

import java.util.Optional;
import java.util.stream.Stream;

public enum TipoDocumento {

    FACTURA("01", "FACTURA"),
    BOLETA("03", "BOLETA"),
    NOTA_CREDITO("07", "NOTA DE CREDITO"),
    NOTA_DEBITO("08", "NOTA DE DEBITO"),
    TICKET_DE_MAQUINA_REGISTRADORA("12", "TICKET DE MAQUINA REGISTRADORA");

    private final String codigo;
    private final String denominacion;

    public String getCodigo() {
        return codigo;
    }

    public String getDenominacion() {
        return denominacion;
    }

    TipoDocumento(String codigo, String denominacion) {
        this.codigo = codigo;
        this.denominacion = denominacion;
    }

    public static Optional<TipoDocumento> getFromCode(String codigo) {
        return Stream.of(TipoDocumento.values())
                .filter(p -> p.getCodigo().equals(codigo))
                .findFirst();
    }

}
