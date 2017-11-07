package org.openfact.report.exceptions;

public class ReportException extends Exception {

    public ReportException() {
    }

    public ReportException(String message) {
        super(message);
    }

    public ReportException(String message, Throwable e) {
        super(message, e);
    }

}
