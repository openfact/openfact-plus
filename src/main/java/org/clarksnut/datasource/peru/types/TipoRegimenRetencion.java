package org.clarksnut.datasource.peru.types;

import java.util.Optional;
import java.util.stream.Stream;

public enum TipoRegimenRetencion {

    TASA_3("01", "TASA 3%");

    private final String codigo;
    private final String denominacion;

    public String getCodigo() {
        return codigo;
    }

    public String getDenominacion() {
        return denominacion;
    }

    TipoRegimenRetencion(String codigo, String denominacion) {
        this.codigo = codigo;
        this.denominacion = denominacion;
    }

    public static Optional<TipoRegimenRetencion> getFromCode(String code) {
        return Stream.of(TipoRegimenRetencion.values())
                .filter(p -> p.getCodigo().equals(code))
                .findFirst();
    }

}
