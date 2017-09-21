package org.openfact.representation.idm;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Map;

public class DocumentRepresentation {

    private Data data;

    public DocumentRepresentation() {
    }

    public DocumentRepresentation(Data data) {
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

        public DocumentRepresentation toSpaceRepresentation() {
            return new DocumentRepresentation(this);
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
        private String id;
        private String assignedId;
        private String documentType;
        private Map<String, String> tags;

        @JsonProperty("created-at")
        private Date createdAt;

        @JsonProperty("updated-at")
        private Date updatedAt;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAssignedId() {
            return assignedId;
        }

        public void setAssignedId(String assignedId) {
            this.assignedId = assignedId;
        }

        public String getDocumentType() {
            return documentType;
        }

        public void setDocumentType(String documentType) {
            this.documentType = documentType;
        }

        public Map<String, String> getTags() {
            return tags;
        }

        public void setTags(Map<String, String> tags) {
            this.tags = tags;
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

        public OwnedBy getOwnedBy() {
            return ownedBy;
        }

        public void setOwnedBy(OwnedBy ownedBy) {
            this.ownedBy = ownedBy;
        }
    }

    public static class OwnedBy {
        private SpaceRepresentation.Data data;
        private GenericLinksRepresentation links;

        public SpaceRepresentation.Data getData() {
            return data;
        }

        public void setData(SpaceRepresentation.Data data) {
            this.data = data;
        }

        public GenericLinksRepresentation getLinks() {
            return links;
        }

        public void setLinks(GenericLinksRepresentation links) {
            this.links = links;
        }
    }

}
