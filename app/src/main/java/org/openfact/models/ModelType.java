package org.openfact.models;

public enum ModelType {
    USER("user"), DOCUMENT("document");

    private String alias;

    ModelType(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

}
