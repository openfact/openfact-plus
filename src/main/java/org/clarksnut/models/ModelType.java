package org.clarksnut.models;

public enum ModelType {

    IDENTITIES("identities"),

    SPACES("spaces"),

    USER("user"),

    UBL_DOCUMENT("ubl-document"),

    THEMES("themes");

    private String alias;

    ModelType(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

}
