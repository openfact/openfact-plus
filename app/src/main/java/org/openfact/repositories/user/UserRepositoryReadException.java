package org.openfact.repositories.user;

public class UserRepositoryReadException extends Exception {

    public UserRepositoryReadException(String message) {
        super(message);
    }

    public UserRepositoryReadException(String message, Throwable e) {
        super(message, e);
    }

}
