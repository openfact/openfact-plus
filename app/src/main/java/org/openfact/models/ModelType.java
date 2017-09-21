package org.openfact.models;

public enum ModelType {

    IDENTITIES("identities"),

    SPACES("spaces"),

    USER("user"),

    UBL_DOCUMENT("ubl-document");

    private String alias;

    ModelType(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

}
