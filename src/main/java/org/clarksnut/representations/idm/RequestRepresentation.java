package org.clarksnut.representations.idm;

import java.util.Date;

public class RequestRepresentation {

    private RequestRepresentation.Data data;

    public RequestRepresentation() {
    }

    public RequestRepresentation(RequestRepresentation.Data data) {
        this.data = data;
    }

    public RequestRepresentation.Data getData() {
        return data;
    }

    public void setData(RequestRepresentation.Data data) {
        this.data = data;
    }

    public static class Data {
        private String id;
        private String type;
        private RequestRepresentation.Attributes attributes;

        public RequestRepresentation toRequestAccessSpaceToRepresentation() {
            return new RequestRepresentation(this);
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

        public RequestRepresentation.Attributes getAttributes() {
            return attributes;
        }

        public void setAttributes(RequestRepresentation.Attributes attributes) {
            this.attributes = attributes;
        }
    }

    public static class Attributes {
        private String space;
        private String user;

        private String message;
        private String status;
        private String scope;

        private Date createdAt;
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

        public String getSpace() {
            return space;
        }

        public void setSpace(String space) {
            this.space = space;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }
    }

    
}
