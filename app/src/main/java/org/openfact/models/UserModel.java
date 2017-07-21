package org.openfact.models;

public interface UserModel {

    String getId();
    String getUsername();

    String getOfflineToken();
    void setOfflineToken(String token);

    boolean isRegistrationCompleted();
    void setRegistrationCompleted(boolean registrationCompleted);
}
