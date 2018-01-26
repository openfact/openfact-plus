package org.clarksnut.representations.idm;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class RequestAccessSpaceToRepresentation {

    private RequestAccessSpaceToRepresentation.Data data;

    public RequestAccessSpaceToRepresentation() {
    }

    public RequestAccessSpaceToRepresentation(RequestAccessSpaceToRepresentation.Data data) {
        this.data = data;
    }

    public RequestAccessSpaceToRepresentation.Data getData() {
        return data;
    }

    public void setData(RequestAccessSpaceToRepresentation.Data data) {
        this.data = data;
    }

    public static class Data {
        private String id;
        private String type;
        private RequestAccessSpaceToRepresentation.Attributes attributes;

        public RequestAccessSpaceToRepresentation toRequestAccessSpaceToRepresentation() {
            return new RequestAccessSpaceToRepresentation(this);
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

        public RequestAccessSpaceToRepresentation.Attributes getAttributes() {
            return attributes;
        }

        public void setAttributes(RequestAccessSpaceToRepresentation.Attributes attributes) {
            this.attributes = attributes;
        }
    }

    public static class Attributes {
        private String message;
        private String status;
        private String scope;

        @JsonProperty("created-at")
        private Date createdAt;

        @JsonProperty("updated-at")
        private Date updatedAt;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }
    }

    
}
