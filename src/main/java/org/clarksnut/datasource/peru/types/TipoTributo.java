package org.clarksnut.datasource.peru.types;

import java.util.Optional;
import java.util.stream.Stream;

public enum TipoTributo {

    IGV("1000"),
    ISC("2000"),
    OTROS("9999");

    private final String codigo;

    public String getCodigo() {
        return codigo;
    }

    TipoTributo(String codigo) {
        this.codigo = codigo;
    }

    public static Optional<TipoTributo> getFromCode(String codigo) {
        return Stream.of(TipoTributo.values())
                .filter(p -> p.getCodigo().equals(codigo))
                .findFirst();
    }

}
