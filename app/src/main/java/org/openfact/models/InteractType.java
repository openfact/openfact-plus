package org.openfact.models;

public enum InteractType {

    SENDER("sender"),

    RECEIVER("receiver");

    private String alias;

    InteractType(String alias) {

        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }
}
