package org.clarksnut.documents.exceptions;

public class IsNotUBLDocumentException extends Exception {

    public IsNotUBLDocumentException() {
    }

    public IsNotUBLDocumentException(String message) {
        super(message);
    }

    public IsNotUBLDocumentException(String message, Throwable e) {
        super(message, e);
    }

}
