package org.openfact.models;

public enum RequestStatusType {

    REQUESTED("requested"),

    ACCEPTED("accepted"),

    REJECTED("rejected");

    private String alias;

    RequestStatusType(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }
}
