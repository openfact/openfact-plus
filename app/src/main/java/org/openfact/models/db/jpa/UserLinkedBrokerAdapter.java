package org.openfact.models.db.jpa;

import org.openfact.models.BrokerType;
import org.openfact.models.UserLinkedBrokerModel;
import org.openfact.models.UserModel;
import org.openfact.models.db.jpa.entity.UserLinkedBrokerEntity;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

public class UserLinkedBrokerAdapter implements UserLinkedBrokerModel {

    private final EntityManager em;
    private final UserModel user;
    private final UserLinkedBrokerEntity userLinkedBroker;

    public UserLinkedBrokerAdapter(EntityManager em, UserModel user, UserLinkedBrokerEntity userLinkedBroker) {
        this.em = em;
        this.user = user;
        this.userLinkedBroker = userLinkedBroker;
    }

    @Override
    public String getId() {
        return userLinkedBroker.getId();
    }

    @Override
    public String getEmail() {
        return userLinkedBroker.getEmail();
    }

    @Override
    public BrokerType getType() {
        return userLinkedBroker.getType();
    }

    @Override
    public LocalDateTime getLastTimeSynchronized() {
        return userLinkedBroker.getLastTimeSynchronized();
    }

    @Override
    public void setLasTimeSynchronized(LocalDateTime lastTimeSynchronized) {
        userLinkedBroker.setLastTimeSynchronized(lastTimeSynchronized);
        em.merge(userLinkedBroker);
    }

    @Override
    public UserModel getUser() {
        return user;
    }

}
