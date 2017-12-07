package org.clarksnut.batchs.broker;

import org.clarksnut.managers.BrokerManager;
import org.clarksnut.models.BrokerType;
import org.clarksnut.models.db.jpa.entity.UserEntity;
import org.clarksnut.models.db.jpa.entity.UserLinkedBrokerEntity;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.jboss.logging.Logger;

import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Inject;
import javax.inject.Named;
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
        UserEntity userEntity = (UserEntity) item;

        Set<UserLinkedBrokerEntity> linkedBrokers = userEntity.getLinkedBrokers();
        Map<String, BrokerType> availableLinkedBrokers = brokerManager.getLinkedBrokers(userEntity.getOfflineToken());

        linkedBrokers.removeIf(p -> !availableLinkedBrokers.containsKey(p.getEmail()));

        Set<String> linkedBrokerEmails = linkedBrokers.stream().map(UserLinkedBrokerEntity::getEmail).collect(Collectors.toSet());
        for (String email : availableLinkedBrokers.keySet()) {
            if (!linkedBrokerEmails.contains(email)) {
                UserLinkedBrokerEntity entity = new UserLinkedBrokerEntity();
                entity.setId(ClarksnutModelUtils.generateId());
                entity.setType(availableLinkedBrokers.get(email));
                entity.setEmail(email);
                entity.setUser(userEntity);

                linkedBrokers.add(entity);
            }
        }

        return userEntity;
    }

}
