package org.openfact.services.services;

public enum SupportedIDP {

    GOOGLE("google"),
    MICROSOFT("microsoft");

    private String microsoft;

    private SupportedIDP(String microsoft) {
        this.microsoft = microsoft;
    }

    public String getBrokerName() {
        return this.microsoft;
    }

}
