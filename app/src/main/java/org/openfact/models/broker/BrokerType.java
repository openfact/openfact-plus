package org.openfact.models.broker;

public enum BrokerType {

    GOOGLE("google"),
    MICROSOFT("microsoft");

    private String microsoft;

    private BrokerType(String microsoft) {
        this.microsoft = microsoft;
    }

    public String getBrokerName() {
        return this.microsoft;
    }

}
