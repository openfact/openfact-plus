package org.openfact.models;

public interface UserModel {

    String getId();

    String getUsername();

    String getFullName();

    void setFullName(String fullName);

    boolean isRegistrationCompleted();

    void setRegistrationCompleted(boolean registrationCompleted);

    String getOfflineRefreshToken();

    void setOfflineRefreshToken(String refreshToken);

}
