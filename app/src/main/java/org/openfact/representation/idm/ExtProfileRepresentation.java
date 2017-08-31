package org.openfact.representation.idm;

public class ExtProfileRepresentation {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        private String type;
        private UserDataAttributesRepresentation attributes;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public UserDataAttributesRepresentation getAttributes() {
            return attributes;
        }

        public void setAttributes(UserDataAttributesRepresentation attributes) {
            this.attributes = attributes;
        }
    }
}
