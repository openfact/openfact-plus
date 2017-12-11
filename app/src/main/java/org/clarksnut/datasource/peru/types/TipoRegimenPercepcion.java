package org.clarksnut.datasource.peru.types;

import java.util.Optional;
import java.util.stream.Stream;

public enum  TipoRegimenPercepcion {

    PERCEPCION_VENTA_INTERNA("01", "PERCEPCION VENTA INTERNA"),
    PERCEPCION_A_LA_ADQUISICION_DE_COMBUSTIBLE("02", "PERCEPCION A LA ADQUISICION DE COMBUSTIBLE"),
    PERCEPCION_REALIZADA_AL_AGENTE_DE_PERCEPCION_CON_TASA_ESPECIAL("03", "PERCEPCION REALIZADA AL AGENTE DE PERCEPCION CON TASA ESPECIAL");

    private final String codigo;
    private final String denominacion;

    public String getCodigo() {
        return codigo;
    }

    public String getDenominacion() {
        return denominacion;
    }

    TipoRegimenPercepcion(String codigo, String denominacion) {
        this.codigo = codigo;
        this.denominacion = denominacion;
    }

    public static Optional<TipoRegimenPercepcion> getFromCode(String code) {
        return Stream.of(TipoRegimenPercepcion.values())
                .filter(p -> p.getCodigo().equals(code))
                .findFirst();
    }

}
