package org.openfact.representation.idm;

public class ResponseFactory {

    public static ResponseRepresentation response(Object data) {
        return ResponseRepresentation.builder()
                .data(data)
                .build();
    }
}
