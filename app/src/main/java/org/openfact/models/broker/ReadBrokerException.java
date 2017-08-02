package org.openfact.models.broker;

public class ReadBrokerException extends Exception {

    public ReadBrokerException(String message) {
        super(message);
    }

    public ReadBrokerException(String message, Throwable e) {
        super(message, e);
    }

}
