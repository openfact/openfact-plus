package org.clarksnut.datasource.peru.types;

import java.util.Optional;
import java.util.stream.Stream;

public enum TipoNotaDebito {

    INTERES("01", "INTERES POR MORA"),
    AUMENTO("02", "AUMENTO EN EL VALOR"),
    PENALIDAD("03", "PENALIDADES / OTROS CONCEPTOS");

    private final String codigo;
    private final String denominacion;

    public String getCodigo() {
        return codigo;
    }

    public String getDenominacion() {
        return denominacion;
    }

    TipoNotaDebito(String codigo, String denominacion) {
        this.codigo = codigo;
        this.denominacion = denominacion;
    }

    public static Optional<TipoNotaDebito> getFromCode(String code) {
        return Stream.of(TipoNotaDebito.values())
                .filter(p -> p.getCodigo().equals(code))
                .findFirst();
    }

}
