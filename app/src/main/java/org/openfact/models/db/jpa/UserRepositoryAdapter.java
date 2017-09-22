package org.openfact.models.db.jpa;

import org.openfact.models.BrokerType;
import org.openfact.models.UserModel;
import org.openfact.models.UserRepositoryModel;
import org.openfact.models.db.JpaModel;
import org.openfact.models.db.jpa.entity.UserLinkedBrokerEntity;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class UserRepositoryAdapter implements UserRepositoryModel, JpaModel<UserLinkedBrokerEntity> {

    private final EntityManager em;
    private final UserLinkedBrokerEntity userRepository;

    public UserRepositoryAdapter(EntityManager em, UserLinkedBrokerEntity userRepository) {
        this.em = em;
        this.userRepository = userRepository;
    }

    @Override
    public UserLinkedBrokerEntity getEntity() {
        return userRepository;
    }

    @Override
    public String getId() {
        return userRepository.getId();
    }

    @Override
    public BrokerType getType() {
        return userRepository.getType();
    }

    @Override
    public String getEmail() {
        return userRepository.getEmail();
    }

    @Override
    public LocalDateTime getLastTimeSynchronized() {
        return userRepository.getLastTimeSynchronized();
    }

    @Override
    public void setLastTimeSynchronized(LocalDateTime dateTime) {
        userRepository.setLastTimeSynchronized(dateTime);
    }

    @Override
    public Map<String, String> getConfig() {
        return new HashMap<>(userRepository.getAttributes());
    }

    @Override
    public void setConfig(Map<String, String> config) {
        userRepository.setAttributes(config);
    }

    @Override
    public UserModel getUser() {
        return new UserAdapter(em, userRepository.getUser());
    }
}
