package org.openfact.models;

import java.util.List;

public interface UserModel {

    String getId();
    String getUsername();

    String getOfflineToken();
    void setOfflineToken(String token);

    boolean isRegistrationCompleted();
    void setRegistrationCompleted(boolean registrationCompleted);

    String getFullName();
    void setFullName(String fullName);

    List<SpaceModel> getOwnedSpaces();
    List<SpaceModel> getMemberSpaces();
    List<SpaceModel> getMemberSpaces(RequestStatus requestStatus);

}
