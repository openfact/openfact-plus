package org.openfact.models;

public class ModelStorageException extends Exception {

    public ModelStorageException(String message) {
        super(message);
    }

    public ModelStorageException(String message, Throwable t) {
        super(message, t);
    }

}
