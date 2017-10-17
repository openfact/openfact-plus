package org.openfact.batchs.broker;

import org.jberet.cdi.StepScoped;
import org.openfact.models.UserLinkedBrokerModel;

import javax.inject.Named;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Named
@StepScoped
public class LinkedBrokers {

    private Map<UserLinkedBrokerModel, LocalDateTime> linkedBrokers = new ConcurrentHashMap<>();

    public LocalDateTime add(UserLinkedBrokerModel itemBroker, LocalDateTime lastSynchronization) {
        return linkedBrokers.put(itemBroker, lastSynchronization);
    }

    public Map<UserLinkedBrokerModel, LocalDateTime> getLinkedBrokers() {
        return linkedBrokers;
    }

}
