package org.clarksnut.representations.idm;

import java.util.Date;

public class SpaceRepresentation {

    private SpaceData data;

    public SpaceRepresentation() {
    }

    public SpaceRepresentation(SpaceData data) {
        this.data = data;
    }

    public SpaceData getData() {
        return data;
    }

    public void setData(SpaceData data) {
        this.data = data;
    }

    public static class SpaceData {
        private String id;
        private String type;
        private SpaceAttributes attributes;
        private SpaceRelationships relationships;
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

        public SpaceAttributes getAttributes() {
            return attributes;
        }

        public void setAttributes(SpaceAttributes attributes) {
            this.attributes = attributes;
        }

        public SpaceRelationships getRelationships() {
            return relationships;
        }

        public void setRelationships(SpaceRelationships relationships) {
            this.relationships = relationships;
        }

        public GenericLinksRepresentation getLinks() {
            return links;
        }

        public void setLinks(GenericLinksRepresentation links) {
            this.links = links;
        }
    }

    public static class SpaceAttributes {
        private String name;
        private String assignedId;
        private String description;


        private Date createdAt;
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

    public static class SpaceRelationships {
        private SpaceOwnedBy ownedBy;
        private SpaceCollaborators collaborators;

        public SpaceOwnedBy getOwnedBy() {
            return ownedBy;
        }

        public void setOwnedBy(SpaceOwnedBy ownedBy) {
            this.ownedBy = ownedBy;
        }

        public SpaceCollaborators getCollaborators() {
            return collaborators;
        }

        public void setCollaborators(SpaceCollaborators collaborators) {
            this.collaborators = collaborators;
        }
    }

    public static class SpaceOwnedBy {
        private UserRepresentation.UserData data;
        private GenericLinksRepresentation links;

        public UserRepresentation.UserData getData() {
            return data;
        }

        public void setData(UserRepresentation.UserData data) {
            this.data = data;
        }

        public GenericLinksRepresentation getLinks() {
            return links;
        }

        public void setLinks(GenericLinksRepresentation links) {
            this.links = links;
        }
    }

    public static class SpaceCollaborators {
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
