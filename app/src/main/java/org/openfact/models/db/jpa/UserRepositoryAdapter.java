package org.openfact.models.db.jpa;

import org.openfact.broker.BrokerType;
import org.openfact.models.UserModel;
import org.openfact.models.UserRepositoryModel;
import org.openfact.models.db.jpa.entity.UserRepositoryEntity;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class UserRepositoryAdapter implements UserRepositoryModel, JpaModel<UserRepositoryEntity> {

    private final EntityManager em;
    private final UserRepositoryEntity userRepository;

    public UserRepositoryAdapter(EntityManager em, UserRepositoryEntity userRepository) {
        this.em = em;
        this.userRepository = userRepository;
    }

    @Override
    public UserRepositoryEntity getEntity() {
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
        return new HashMap<>(userRepository.getConfig());
    }

    @Override
    public void setConfig(Map<String, String> config) {
        userRepository.setConfig(config);
    }

    @Override
    public UserModel getUser() {
        return new UserAdapter(em, userRepository.getUser());
    }
}
