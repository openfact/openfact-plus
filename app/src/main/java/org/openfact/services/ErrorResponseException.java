package org.openfact.services;

import javax.ejb.ApplicationException;
import javax.ws.rs.core.Response.Status;

@ApplicationException(rollback = true)
public class ErrorResponseException extends Exception {

    private Status status;

    public ErrorResponseException(String message) {
        super(message);
        this.status = Status.BAD_REQUEST;
    }

    public ErrorResponseException(String message, Status status) {
        super(message);
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

}
