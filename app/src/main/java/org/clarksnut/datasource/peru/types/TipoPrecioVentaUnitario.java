package org.clarksnut.datasource.peru.types;

import java.util.Optional;
import java.util.stream.Stream;

public enum TipoPrecioVentaUnitario {

    PRECIO_UNITARIO("01"),
    VALOR_REFERENCIAL_UNITARIO_EN_OPERACIONES_NO_ONEROSAS("02");

    private final String code;

    public String getCode() {
        return code;
    }

    TipoPrecioVentaUnitario(String code) {
        this.code = code;
    }

    public static Optional<TipoPrecioVentaUnitario> getByCode(String code) {
        return Stream.of(TipoPrecioVentaUnitario.values())
                .filter(p -> p.code.equals(code))
                .findFirst();
    }
}
