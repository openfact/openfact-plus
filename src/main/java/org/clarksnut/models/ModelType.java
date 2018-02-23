package org.clarksnut.models;

public enum ModelType {

    IDENTITIES("identities"),

    SPACES("spaces"),

    USER("user"),

    UBL_DOCUMENT("ubl-document"),

    THEMES("themes"),

    REQUEST_ACCESS("request-access"),;

    private String alias;

    ModelType(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

}
