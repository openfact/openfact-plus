package org.clarksnut.datasource.peru.types;

import java.util.Optional;
import java.util.stream.Stream;

public enum TipoNotaCredito {

    ANULACION_DE_LA_OPERACION("01", "ANULACION DE LA OPERACION"),
    ANULACION_POR_ERROR_EN_EL_RUC("02", "ANULACION POR ERROR EN EL RUC"),
    CORRECCION_POR_ERROR_EN_LA_DESCRIPCION("03", "CORRECCION POR ERROR EN LA DESCRIPCION"),
    DESCUENTO_GLOBAL("04", "DESCUENTO GLOBAL"),
    DESCUENTO_POR_ITEM("05", "DESCUENTO POR ITEM"),
    DEVOLUCION_TOTAL("06", "DEVOLUCION TOTAL"),
    DEVOLUCION_POR_ITEM("07", "DEVOLUCION POR ITEM"),
    BONIFICACION("08", "BONIFICACION"),
    DISMINUCION_EN_EL_VALOR("09", "DISMINUCION EN EL VALOR"),
    OTROS_CONCEPTOS("10", "OTROS CONCEPTOS");

    private final String codigo;
    private final String denominacion;

    public String getCodigo() {
        return codigo;
    }

    public String getDenominacion() {
        return denominacion;
    }

    TipoNotaCredito(String codigo, String denominacion) {
        this.codigo = codigo;
        this.denominacion = denominacion;
    }

    public static Optional<TipoNotaCredito> getByCode(String code) {
        return Stream.of(TipoNotaCredito.values())
                .filter(p -> p.getCodigo().equals(code))
                .findFirst();
    }

}
