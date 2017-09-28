package org.openfact.models;

import java.io.IOException;

public class ModelFetchException extends ModelStorageException {

    public ModelFetchException(String message) {
        super(message);
    }

    public ModelFetchException(String message, Throwable t) {
        super(message, t);
    }
}
