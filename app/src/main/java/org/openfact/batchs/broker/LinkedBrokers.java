package org.openfact.batchs.broker;

import org.jberet.cdi.StepScoped;
import org.openfact.models.db.jpa.entity.UserLinkedBrokerEntity;

import javax.inject.Named;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Named
@StepScoped
public class LinkedBrokers {

    private Map<UserLinkedBrokerEntity, LocalDateTime> linkedBrokers = new ConcurrentHashMap<>();

    public LocalDateTime add(UserLinkedBrokerEntity itemBroker, LocalDateTime lastSynchronization) {
        return linkedBrokers.put(itemBroker, lastSynchronization);
    }

    public Map<UserLinkedBrokerEntity, LocalDateTime> getLinkedBrokers() {
        return linkedBrokers;
    }

}
