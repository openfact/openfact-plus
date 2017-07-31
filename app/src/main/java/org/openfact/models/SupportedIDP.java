package org.openfact.models;

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
