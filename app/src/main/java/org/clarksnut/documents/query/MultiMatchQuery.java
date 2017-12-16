package org.clarksnut.documents.query;

public class MultiMatchQuery implements Query {

    private final Object text;
    private final String[] fieldNames;

    public MultiMatchQuery(Object text, String... fieldNames) {
        this.text = text;
        this.fieldNames = fieldNames;
    }

    @Override
    public String getQueryName() {
        return "MultiMatch";
    }

    public Object getText() {
        return text;
    }

    public String[] getFieldNames() {
        return fieldNames;
    }

}
