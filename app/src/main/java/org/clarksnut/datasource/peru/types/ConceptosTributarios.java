package org.clarksnut.datasource.peru.types;

import java.util.Optional;
import java.util.stream.Stream;

public enum ConceptosTributarios {

    TOTAL_VALOR_VENTA_OPERACIONES_GRAVADAS("1001"),
    TOTAL_VALOR_VENTA_OPERACIONES_INAFECTAS("1002"),
    TOTAL_VALOR_VENTA_OPERACIONES_EXONERADAS("1003"),
    TOTAL_VALOR_VENTA_OPERACIONES_GRATUITAS("1004"),
    TOTAL_DESCUENTOS("2005");

    private final String code;

    public String getCode() {
        return code;
    }

    ConceptosTributarios(String code) {
        this.code = code;
    }

    public static Optional<ConceptosTributarios> getByCode(String code) {
        return Stream.of(ConceptosTributarios.values())
                .filter(p -> p.code.equals(code))
                .findFirst();
    }
}
