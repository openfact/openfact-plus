package org.openfact.models;

public class StorageException extends Exception {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable t) {
        super(message, t);
    }

}
