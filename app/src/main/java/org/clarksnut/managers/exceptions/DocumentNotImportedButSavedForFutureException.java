package org.clarksnut.managers.exceptions;

public class DocumentNotImportedButSavedForFutureException extends Exception {

    public DocumentNotImportedButSavedForFutureException() {
    }

    public DocumentNotImportedButSavedForFutureException(String message) {
        super(message);
    }

    public DocumentNotImportedButSavedForFutureException(String message, Throwable e) {
        super(message, e);
    }

}
