package org.clarksnut.representations.idm;

import java.util.Date;

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

    }

    public static class UserAttributesRepresentation {

        // The id of the corresponding User
        private String userID;

        // The user's full name
        private String fullName;

        // The avatar image for the user
        private String imageURL;

        // The username
        private String username;

        // Whether the registration has been completed
        private Boolean registrationCompleted;

        // The email
        private String email;

        // The bio
        private String bio;

        // The url
        private String url;

        // The company
        private String company;

        // The IDP provided this identity
        private String providerType;

        private String defaultLanguage;

        // The date of creation of the user

        private Date createdAt;

        // The date of update of the user

        private Date updatedAt;

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
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

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getImageURL() {
            return imageURL;
        }

        public void setImageURL(String imageURL) {
            this.imageURL = imageURL;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Boolean getRegistrationCompleted() {
            return registrationCompleted;
        }

        public void setRegistrationCompleted(Boolean registrationCompleted) {
            this.registrationCompleted = registrationCompleted;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getProviderType() {
            return providerType;
        }

        public void setProviderType(String providerType) {
            this.providerType = providerType;
        }

        public String getDefaultLanguage() {
            return defaultLanguage;
        }

        public void setDefaultLanguage(String defaultLanguage) {
            this.defaultLanguage = defaultLanguage;
        }
    }

}
