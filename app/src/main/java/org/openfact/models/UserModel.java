package org.openfact.models;

import org.openfact.broker.BrokerType;

import java.util.List;
import java.util.Set;

public interface UserModel {

    String getId();
    String getUsername();

    String getFullName();
    void setFullName(String fullName);

    boolean isRegistrationCompleted();
    void setRegistrationCompleted(boolean registrationCompleted);

    String getOfflineRefreshToken();
    void setOfflineRefreshToken(String refreshToken);

    Set<SpaceModel> getOwnedSpaces();
    Set<SharedSpaceModel> getSharedSpaces();

    List<RequestAccessToSpaceModel> getSpaceRequests();

    UserRepositoryModel addRepository(String email, BrokerType type);
    List<UserRepositoryModel> getRepositories();
    boolean removeAllRepositories();

}
