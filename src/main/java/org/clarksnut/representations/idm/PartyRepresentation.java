package org.clarksnut.representations.idm;

public class PartyRepresentation {

    private Data data;

    public PartyRepresentation() {
    }

    public PartyRepresentation(Data data) {
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
        private Attributes attributes;

        public PartyRepresentation toSpaceRepresentation() {
            return new PartyRepresentation(this);
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

        public Attributes getAttributes() {
            return attributes;
        }

        public void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }

    }

    public static class Attributes {
        private String assignedId;
        private String name;

        public String getAssignedId() {
            return assignedId;
        }

        public void setAssignedId(String assignedId) {
            this.assignedId = assignedId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
