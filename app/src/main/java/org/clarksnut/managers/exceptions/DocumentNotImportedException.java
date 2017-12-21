package org.clarksnut.managers.exceptions;

public class DocumentNotImportedException extends Exception {

    public DocumentNotImportedException() {
    }

    public DocumentNotImportedException(String message) {
        super(message);
    }

    public DocumentNotImportedException(String message, Throwable e) {
        super(message, e);
    }

}
