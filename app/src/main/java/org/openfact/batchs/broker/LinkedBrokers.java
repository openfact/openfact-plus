package org.openfact.batchs.broker;

import org.jberet.cdi.StepScoped;
import org.openfact.models.db.jpa.entity.UserLinkedBrokerEntity;

import javax.inject.Named;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@StepScoped
public class LinkedBrokers {

    private List<UserLinkedBrokerEntity> linkedBrokers = new CopyOnWriteArrayList<>();

    public boolean add(UserLinkedBrokerEntity itemBroker) {
        return linkedBrokers.add(itemBroker);
    }

    public List<UserLinkedBrokerEntity> getLinkedBrokers() {
        return linkedBrokers;
    }

}
