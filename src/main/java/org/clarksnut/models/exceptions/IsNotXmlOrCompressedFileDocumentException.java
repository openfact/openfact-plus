package org.clarksnut.models.exceptions;

public class IsNotXmlOrCompressedFileDocumentException extends Exception {
    public IsNotXmlOrCompressedFileDocumentException() {
    }

    public IsNotXmlOrCompressedFileDocumentException(String message) {
        super(message);
    }

    public IsNotXmlOrCompressedFileDocumentException(String message, Throwable e) {
        super(message, e);
    }
}
