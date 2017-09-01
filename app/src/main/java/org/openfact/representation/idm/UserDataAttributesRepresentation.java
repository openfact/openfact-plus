package org.openfact.representation.idm;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class UserDataAttributesRepresentation {

    // The id of the corresponding User
    private String userID;

    // The id of the corresponding Identity
    private String identityID;

    // The date of creation of the user
    private LocalDateTime created_at;

    // The date of update of the user
    private LocalDateTime updated_at;

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

    private List<SpaceRepresentation> spaces;
    private List<RequestAccessToSpaceRepresentation> spaceRequests;

    private String refreshToken;
    private ContextInformationRepresentation contextInformation;

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

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
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

    public List<SpaceRepresentation> getSpaces() {
        return spaces;
    }

    public void setSpaces(List<SpaceRepresentation> spaces) {
        this.spaces = spaces;
    }

    public List<RequestAccessToSpaceRepresentation> getSpaceRequests() {
        return spaceRequests;
    }

    public void setSpaceRequests(List<RequestAccessToSpaceRepresentation> spaceRequests) {
        this.spaceRequests = spaceRequests;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public ContextInformationRepresentation getContextInformation() {
        return contextInformation;
    }

    public void setContextInformation(ContextInformationRepresentation contextInformation) {
        this.contextInformation = contextInformation;
    }
}
