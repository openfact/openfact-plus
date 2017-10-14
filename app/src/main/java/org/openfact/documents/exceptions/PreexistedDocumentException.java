package org.openfact.documents.exceptions;

public class PreexistedDocumentException extends Exception {

    public PreexistedDocumentException(String message) {
        super(message);
    }

    public PreexistedDocumentException(String message, Throwable e) {
        super(message, e);
    }
    
}
