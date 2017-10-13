package org.openfact.documents;

public class ModelUnsupportedTypeException extends Exception {

    public ModelUnsupportedTypeException(String message) {
        super(message);
    }

    public ModelUnsupportedTypeException(String message, Throwable e) {
        super(message, e);
    }
    
}
