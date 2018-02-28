package org.clarksnut.representations.idm;

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
        private UserRepresentation.UserAttributesRepresentation attributes;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public UserRepresentation.UserAttributesRepresentation getAttributes() {
            return attributes;
        }

        public void setAttributes(UserRepresentation.UserAttributesRepresentation attributes) {
            this.attributes = attributes;
        }
    }
}
