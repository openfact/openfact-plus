package org.openfact.representation.idm;

import java.util.Date;

public class UserDataAttributesRepresentation {

    // The id of the corresponding User
    private String userID;

    // The id of the corresponding Identity
    private String identityID;

    // The date of creation of the user
    private Date created_at;

    // The date of update of the user
    private Date updated_at;

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


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getIdentityID() {
        return identityID;
    }

    public void setIdentityID(String identityID) {
        this.identityID = identityID;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
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

    public Boolean isRegistrationCompleted() {
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
}
