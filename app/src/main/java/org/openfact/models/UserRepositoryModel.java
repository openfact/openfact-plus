package org.openfact.models;

import org.openfact.broker.BrokerType;

import java.time.LocalDateTime;
import java.util.Map;

public interface UserRepositoryModel {

    String getId();

    String getEmail();

    BrokerType getType();

    LocalDateTime getLastTimeSynchronized();

    void setLastTimeSynchronized(LocalDateTime dateTime);

    Map<String, String> getConfig();

    void setConfig(Map<String, String> config);

    UserModel getUser();

}
