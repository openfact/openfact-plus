package org.openfact.models;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface UserModel {

    String USERNAME = "username";

    String getId();
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

    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();

    Set<SpaceModel> getOwnedSpaces();
    Set<SharedSpaceModel> getSharedSpaces();

    List<RequestAccessToSpaceModel> getSpaceRequests();

    UserRepositoryModel addRepository(String email, BrokerType type);
    List<UserRepositoryModel> getRepositories();
    boolean removeAllRepositories();

}
