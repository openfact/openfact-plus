package org.clarksnut.models;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface UserModel {

    String USERNAME = "username";
    String FULL_NAME = "fullName";

    String getId();
    String getUsername();
    String getProviderType();
    String getIdentityId();

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

    String getDefaultLanguage();
    void setDefaultLanguage(String language);

    Date getCreatedAt();
    Date getUpdatedAt();

    List<SpaceModel> getOwnedSpaces();
    List<SpaceModel> getOwnedSpaces(int offset, int limit);

    List<SpaceModel> getCollaboratedSpaces();
    List<SpaceModel> getCollaboratedSpaces(int offset, int limit);

    List<SpaceModel> getAllPermittedSpaces();

    Set<String> getFavoriteSpaces();
    void setFavoriteSpaces(Set<String> spaces);
}
