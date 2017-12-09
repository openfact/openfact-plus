package org.clarksnut.datasource.peru.types;

import java.util.Optional;
import java.util.stream.Stream;

public enum TipoDocumentoEntidad {

    DOC_TRIB_NO_DOM_SIN_RUC("0", "NO DOMICILIADO"),
    DNI("1", "DNI"),
    EXTRANJERIA("4", "C.EXTRANJERIA"),
    RUC("6", "RUC"),
    PASAPORTE("7", "PASSAPORTE"),
    DEC_DIPLOMATICA("A", "CED.DIPLOMATICA");

    private final String codigo;
    private final String denominacion;

    public String getCodigo() {
        return codigo;
    }

    public String getDenominacion() {
        return denominacion;
    }

    TipoDocumentoEntidad(String codigo, String denominacion) {
        this.codigo = codigo;
        this.denominacion = denominacion;
    }

    public static Optional<TipoDocumentoEntidad> getByCode(String code) {
        return Stream.of(TipoDocumentoEntidad.values())
                .filter(p -> p.getCodigo().equals(code))
                .findFirst();
    }

}
