package org.openfact.models;

import java.util.List;
import java.util.Set;

public interface UserModel {

    String getId();
    String getUsername();

    String getFullName();
    void setFullName(String fullName);

    boolean isRegistrationCompleted();
    void setRegistrationCompleted(boolean registrationCompleted);

    String getOfflineToken();
    void setOfflineToken(String token);

    Set<SpaceModel> getOwnedSpaces();
    Set<SharedSpaceModel> getSharedSpaces();

    List<RequestAccessToSpaceModel> getSpaceRequests();

}
