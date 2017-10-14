package org.openfact.documents.exceptions;

public class UnsupportedDocumentTypeException extends Exception {

    public UnsupportedDocumentTypeException(String message) {
        super(message);
    }

    public UnsupportedDocumentTypeException(String message, Throwable e) {
        super(message, e);
    }
    
}
