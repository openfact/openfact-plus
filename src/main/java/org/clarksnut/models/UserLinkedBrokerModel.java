package org.clarksnut.models;

import java.time.LocalDateTime;

public interface UserLinkedBrokerModel {

    String getId();

    String getEmail();

    BrokerType getType();

    LocalDateTime getLastTimeSynchronized();

    void setLasTimeSynchronized(LocalDateTime lastTimeSynchronized);

    UserModel getUser();
}
