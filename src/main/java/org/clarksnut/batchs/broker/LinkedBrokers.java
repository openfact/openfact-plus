package org.clarksnut.batchs.broker;

import org.clarksnut.models.UserLinkedBrokerModel;
import org.jberet.cdi.StepScoped;

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
