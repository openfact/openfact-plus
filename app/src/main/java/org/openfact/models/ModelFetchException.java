package org.openfact.models;

public class ModelFetchException extends Exception {

    public ModelFetchException(String message) {
        super(message);
    }

    public ModelFetchException(String message, Throwable t) {
        super(message, t);
    }

}
