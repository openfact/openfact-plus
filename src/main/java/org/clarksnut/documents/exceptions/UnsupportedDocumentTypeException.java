package org.clarksnut.documents.exceptions;

/**
 * Document is not an xml or is not supported by the system
 */
public class UnsupportedDocumentTypeException extends Exception {

    public UnsupportedDocumentTypeException() {
    }

    public UnsupportedDocumentTypeException(String message) {
        super(message);
    }

    public UnsupportedDocumentTypeException(String message, Throwable e) {
        super(message, e);
    }

}
