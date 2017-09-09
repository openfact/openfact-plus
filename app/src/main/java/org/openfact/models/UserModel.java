package org.openfact.models;

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

    String getOfflineRefreshToken();
    void setOfflineRefreshToken(String refreshToken);

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

    Date getCreatedAt();
    Date getUpdatedAt();

    Set<SpaceModel> getOwnedSpaces();
//    Set<SharedSpaceModel> getSharedSpaces();

//    Set<String> getRecentSpaces();
//    void setRecentSpaces(Set<String> recentSpaces);
    JsonNode getContextInformation();
    void setContextInformation(JsonNode contextInformation);

//    List<RequestAccessToSpaceModel> getSpaceRequests();
//
//    UserRepositoryModel addRepository(String email, BrokerType type);
//    List<UserRepositoryModel> getRepositories();
//    boolean removeAllRepositories();

}
