package org.openfact.representation.idm;

import org.openfact.models.ModelType;

public class ResponseRepresentation {

    private String type;
    private Object data;

    public ResponseRepresentation() {

    }

    public ResponseRepresentation(Builder builder) {
        this.setType(builder.type);
        this.setData(builder.data);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static class Builder {

        private String type;
        private Object data;

        public Builder type(ModelType type) {
            this.type = type.getAlias();
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public ResponseRepresentation build() {
            return new ResponseRepresentation(this);
        }
    }
}
