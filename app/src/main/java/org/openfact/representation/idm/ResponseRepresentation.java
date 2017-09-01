package org.openfact.representation.idm;

import org.openfact.models.ModelType;

public class ResponseRepresentation {

    private Object data;

    public ResponseRepresentation() {

    }

    public ResponseRepresentation(Builder builder) {
        this.setData(builder.data);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static class Builder {

        private Object data;

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public ResponseRepresentation build() {
            return new ResponseRepresentation(this);
        }
    }
}
