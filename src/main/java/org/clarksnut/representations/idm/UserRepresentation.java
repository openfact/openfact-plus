package org.clarksnut.representations.idm;

public class UserRepresentation {

    private Data data;

    public UserRepresentation() {
    }

    public UserRepresentation(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String id;
        private String type;
        private String scope;
        private UserAttributesRepresentation attributes;
        private GenericLinksRepresentation links;

        public UserRepresentation toUserRepresentation() {
            return new UserRepresentation(this);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public UserAttributesRepresentation getAttributes() {
            return attributes;
        }

        public void setAttributes(UserAttributesRepresentation attributes) {
            this.attributes = attributes;
        }

        public GenericLinksRepresentation getLinks() {
            return links;
        }

        public void setLinks(GenericLinksRepresentation links) {
            this.links = links;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }
    }

}
