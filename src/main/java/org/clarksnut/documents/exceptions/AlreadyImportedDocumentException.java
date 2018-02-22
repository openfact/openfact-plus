package org.clarksnut.documents.exceptions;

public class AlreadyImportedDocumentException extends Exception {

    public AlreadyImportedDocumentException() {
    }

    public AlreadyImportedDocumentException(String message) {
        super(message);
    }

    public AlreadyImportedDocumentException(String message, Throwable e) {
        super(message, e);
    }

}
