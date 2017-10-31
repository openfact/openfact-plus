package org.openfact.representations.idm;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
        private DocumentLink links;

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

        public DocumentLink getLinks() {
            return links;
        }

        public void setLinks(DocumentLink links) {
            this.links = links;
        }
    }

    public static class Attributes {
        private String id;
        private String assignedId;
        private String type;
        private String currency;
        private Float amount;
        private Date issueDate;
        private String supplierName;
        private String supplierAssignedId;
        private String customerName;
        private String customerAssignedId;
        private String provider;
        private Boolean stared;
        private Set<String> tags;


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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Set<String> getTags() {
            return tags;
        }

        public void setTags(Set<String> tags) {
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

        public void setAmount(Float amount) {
            this.amount = amount;
        }

        public Float getAmount() {
            return amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public Date getIssueDate() {
            return issueDate;
        }

        public void setIssueDate(Date issueDate) {
            this.issueDate = issueDate;
        }

        public String getSupplierName() {
            return supplierName;
        }

        public void setSupplierName(String supplierName) {
            this.supplierName = supplierName;
        }

        public String getSupplierAssignedId() {
            return supplierAssignedId;
        }

        public void setSupplierAssignedId(String supplierAssignedId) {
            this.supplierAssignedId = supplierAssignedId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getCustomerAssignedId() {
            return customerAssignedId;
        }

        public void setCustomerAssignedId(String customerAssignedId) {
            this.customerAssignedId = customerAssignedId;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public Boolean getStared() {
            return stared;
        }

        public void setStared(Boolean stared) {
            this.stared = stared;
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
        private List<SpaceRepresentation.Data> data;
//        private GenericLinksRepresentation links;

        public List<SpaceRepresentation.Data> getData() {
            return data;
        }

        public void setData(List<SpaceRepresentation.Data> data) {
            this.data = data;
        }

//        public GenericLinksRepresentation getLinks() {
//            return links;
//        }
//
//        public void setLinks(GenericLinksRepresentation links) {
//            this.links = links;
//        }
    }

    public static class DocumentLink {
        private String self;
        private String filelink;

        public String getSelf() {
            return self;
        }

        public void setSelf(String self) {
            this.self = self;
        }

        public String getFilelink() {
            return filelink;
        }

        public void setFilelink(String filelink) {
            this.filelink = filelink;
        }
    }

}
