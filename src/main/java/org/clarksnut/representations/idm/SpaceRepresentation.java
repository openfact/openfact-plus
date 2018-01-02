package org.clarksnut.representations.idm;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class SpaceRepresentation {

    private Data data;

    public SpaceRepresentation() {
    }

    public SpaceRepresentation(Data data) {
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
        private Relationships relationships;
        private GenericLinksRepresentation links;

        public SpaceRepresentation toSpaceRepresentation() {
            return new SpaceRepresentation(this);
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

        public Relationships getRelationships() {
            return relationships;
        }

        public void setRelationships(Relationships relationships) {
            this.relationships = relationships;
        }

        public GenericLinksRepresentation getLinks() {
            return links;
        }

        public void setLinks(GenericLinksRepresentation links) {
            this.links = links;
        }
    }

    public static class Attributes {
        private String name;
        private String assignedId;
        private String description;

        @JsonProperty("created-at")
        private Date createdAt;

        @JsonProperty("updated-at")
        private Date updatedAt;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAssignedId() {
            return assignedId;
        }

        public void setAssignedId(String assignedId) {
            this.assignedId = assignedId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        public Date getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

    public static class Relationships {
        @JsonProperty("owned-by")
        private OwnedBy ownedBy;
        private Collaborators collaborators;

        public OwnedBy getOwnedBy() {
            return ownedBy;
        }

        public void setOwnedBy(OwnedBy ownedBy) {
            this.ownedBy = ownedBy;
        }

        public Collaborators getCollaborators() {
            return collaborators;
        }

        public void setCollaborators(Collaborators collaborators) {
            this.collaborators = collaborators;
        }
    }

    public static class OwnedBy {
        private UserRepresentation.Data data;
        private GenericLinksRepresentation links;

        public UserRepresentation.Data getData() {
            return data;
        }

        public void setData(UserRepresentation.Data data) {
            this.data = data;
        }

        public GenericLinksRepresentation getLinks() {
            return links;
        }

        public void setLinks(GenericLinksRepresentation links) {
            this.links = links;
        }
    }

    public static class Collaborators {
        private GenericLinksRepresentation links;

        public GenericLinksRepresentation getLinks() {
            return links;
        }

        public void setLinks(GenericLinksRepresentation links) {
            this.links = links;
        }
    }

    public static class SpaceLink {
        private String self;

        public String getSelf() {
            return self;
        }

        public void setSelf(String self) {
            this.self = self;
        }
    }

}
