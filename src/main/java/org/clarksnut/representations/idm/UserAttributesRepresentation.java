package org.clarksnut.representations.idm;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class UserAttributesRepresentation {

    // The id of the corresponding User
    private String userID;

    // The id of the corresponding Identity
    private String identityID;

    // The date of creation of the user

    private Date createdAt;

    // The date of update of the user

    private Date updatedAt;

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

    private String refreshToken;

    private Set<String> favoriteSpaces;
    private Set<String> ownedSpaces;
    private Set<String> collaboratedSpaces;

    private String defaultLanguage;

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

    public List<SpaceRepresentation> getSpaces() {
        return spaces;
    }

    public void setSpaces(List<SpaceRepresentation> spaces) {
        this.spaces = spaces;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Set<String> getFavoriteSpaces() {
        return favoriteSpaces;
    }

    public void setFavoriteSpaces(Set<String> favoriteSpaces) {
        this.favoriteSpaces = favoriteSpaces;
    }

    public Set<String> getOwnedSpaces() {
        return ownedSpaces;
    }

    public void setOwnedSpaces(Set<String> ownedSpaces) {
        this.ownedSpaces = ownedSpaces;
    }

    public Set<String> getCollaboratedSpaces() {
        return collaboratedSpaces;
    }

    public void setCollaboratedSpaces(Set<String> collaboratedSpaces) {
        this.collaboratedSpaces = collaboratedSpaces;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }
}
