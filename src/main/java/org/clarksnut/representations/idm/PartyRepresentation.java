package org.clarksnut.representations.idm;

public class PartyRepresentation {

    private PartyData data;

    public PartyRepresentation() {
    }

    public PartyRepresentation(PartyData data) {
        this.data = data;
    }

    public PartyData getData() {
        return data;
    }

    public void setData(PartyData data) {
        this.data = data;
    }

    public static class PartyData {
        private String id;
        private String type;
        private PartyAttributes attributes;

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

        public PartyAttributes getAttributes() {
            return attributes;
        }

        public void setAttributes(PartyAttributes attributes) {
            this.attributes = attributes;
        }

    }

    public static class PartyAttributes {
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
