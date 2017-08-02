package org.openfact.models;

public class ParseExceptionModel extends Exception {

    public ParseExceptionModel(String message) {
        super(message);
    }

    public ParseExceptionModel(String message, Throwable e) {
        super(message, e);
    }
    
}
