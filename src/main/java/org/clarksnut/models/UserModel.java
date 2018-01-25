package org.clarksnut.models;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface UserModel {

    String USERNAME = "username";

    String getId();

    String getIdentityID();

    String getProviderType();

    String getUsername();

    String getFullName();

    void setFullName(String fullName);

    boolean isRegistrationCompleted();

    void setRegistrationCompleted(boolean registrationCompleted);

    String getImageURL();

    void setImageURL(String imageURL);

    String getBio();

    void setBio(String bio);

    String getEmail();

    void setEmail(String email);

    String getCompany();

    void setCompany(String company);

    String getUrl();

    void setUrl(String url);

    String getEmailTheme();

    void setEmailTheme(String emailTheme);

    String getDefaultLanguage();

    void setDefaultLanguage(String language);

    Date getCreatedAt();

    Date getUpdatedAt();

    Set<SpaceModel> getOwnedSpaces();

    Set<SpaceModel> getCollaboratedSpaces();

    Set<SpaceModel> getAllPermitedSpaces();

    Set<String> getFavoriteSpaces();

    void setFavoriteSpaces(Set<String> spaces);

    void setContextInformation(JsonNode contextInformation);

    List<UserLinkedBrokerModel> getLinkedBrokers();
}
