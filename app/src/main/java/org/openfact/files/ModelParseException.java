package org.openfact.files;

public class ModelParseException extends Exception {

    public ModelParseException(String message) {
        super(message);
    }

    public ModelParseException(String message, Throwable e) {
        super(message, e);
    }
    
}
