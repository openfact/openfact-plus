package org.openfact.models;

import java.util.List;
import java.util.Set;

public interface UserModel {

    String getId();
    String getUsername();

    String getOfflineToken();
    void setOfflineToken(String token);

    boolean isRegistrationCompleted();
    void setRegistrationCompleted(boolean registrationCompleted);

    String getFullName();
    void setFullName(String fullName);
    
}
