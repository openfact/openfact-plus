package org.clarksnut.documents.exceptions;

public class ImpossibleToUnmarshallException extends Exception {

    public ImpossibleToUnmarshallException() {
    }

    public ImpossibleToUnmarshallException(String message) {
        super(message);
    }

    public ImpossibleToUnmarshallException(String message, Throwable e) {
        super(message, e);
    }

}
