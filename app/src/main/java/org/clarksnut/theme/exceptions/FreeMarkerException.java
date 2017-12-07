package org.clarksnut.theme.exceptions;

public class FreeMarkerException extends Exception {

    public FreeMarkerException(String message) {
        super(message);
    }

    public FreeMarkerException(String message, Throwable cause) {
        super(message, cause);
    }
}
