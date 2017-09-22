package org.openfact.batchs.broker;

import org.jboss.logging.Logger;
import org.openfact.models.BrokerType;
import org.openfact.models.db.jpa.entity.UserEntity;
import org.openfact.models.db.jpa.entity.UserLinkedBrokerEntity;
import org.openfact.models.utils.OpenfactModelUtils;
import org.openfact.services.managers.BrokerManager;

import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Named
public class RefreshLinkedBrokersProcessor implements ItemProcessor {

    private static final Logger logger = Logger.getLogger(RefreshLinkedBrokersProcessor.class);

    @Inject
    private BrokerManager brokerManager;

    @Override
    public UserEntity processItem(Object item) throws Exception {
        UserEntity userEntity;
        if (item instanceof UserEntity) {
            userEntity = (UserEntity) item;
        } else {
            throw new IllegalStateException("Could not cast to UserEntity class");
        }

        Set<UserLinkedBrokerEntity> linkedBrokers = userEntity.getLinkedBrokers();
        Map<String, BrokerType> availableLinkedBrokers = brokerManager.getLinkedBrokers(userEntity.getOfflineToken());

        Set<UserLinkedBrokerEntity> toRemove = linkedBrokers.stream()
                .filter(linkedBrokerEntity -> availableLinkedBrokers.containsKey(linkedBrokerEntity.getEmail()))
                .collect(Collectors.toSet());

        Set<String> linkedBrokerKeys = linkedBrokers.stream()
                .map(UserLinkedBrokerEntity::getEmail)
                .collect(Collectors.toSet());
        Set<UserLinkedBrokerEntity> toCreate = availableLinkedBrokers.keySet()
                .stream()
                .filter(linkedBrokerKeys::contains)
                .map(email -> {
                    UserLinkedBrokerEntity entity = new UserLinkedBrokerEntity();
                    entity.setId(OpenfactModelUtils.generateId());
                    entity.setType(availableLinkedBrokers.get(email));
                    entity.setEmail(email);
                    entity.setUser(userEntity);
                    entity.setAttributes(new HashMap<>());
                    return entity;
                }).collect(Collectors.toSet());

        linkedBrokers.removeAll(toRemove);
        linkedBrokers.addAll(toCreate);
        return userEntity;
    }

}
