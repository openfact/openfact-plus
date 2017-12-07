package org.clarksnut.files.exceptions;

public class FileFetchException extends Exception {

    public FileFetchException(String message) {
        super(message);
    }

    public FileFetchException(String message, Throwable t) {
        super(message, t);
    }

}
