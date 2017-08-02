package org.openfact.models;

import org.openfact.models.broker.BrokerType;

import java.time.LocalDateTime;
import java.util.Map;

public interface UserRepositoryModel {

    String getId();

    BrokerType getType();

    String getEmail();

    LocalDateTime getLastTimeSynchronized();

    void setLastTimeSynchronized(LocalDateTime dateTime);

    Map<String, String> getConfig();

    void setConfig(Map<String, String> config);
}
