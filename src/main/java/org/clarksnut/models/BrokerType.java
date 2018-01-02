package org.clarksnut.models;

public enum BrokerType {

    GOOGLE("google");

    private String alias;

    BrokerType(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return this.alias;
    }

}
