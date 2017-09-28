package org.openfact.models;

public enum PermissionType {

    OWNER("owner"),

    READ("read");

    private String alias;

    PermissionType(String alias) {

        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }
}
