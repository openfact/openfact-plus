package org.openfact.representation.idm;

import org.openfact.models.ModelType;

public class ResponseFactory {

    public static ResponseRepresentation response(ModelType type, Object data) {
        return ResponseRepresentation.builder()
                .type(type)
                .data(data)
                .build();
    }
}
