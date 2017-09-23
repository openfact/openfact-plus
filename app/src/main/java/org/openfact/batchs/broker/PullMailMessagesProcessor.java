package org.openfact.batchs.broker;

import org.jboss.logging.Logger;
import org.openfact.models.BrokerType;
import org.openfact.models.db.jpa.entity.UserEntity;
import org.openfact.models.db.jpa.entity.UserLinkedBrokerEntity;
import org.openfact.models.utils.OpenfactModelUtils;
import org.openfact.repositories.user.MailQuery;
import org.openfact.repositories.user.MailReader;
import org.openfact.repositories.user.MailRepository;
import org.openfact.repositories.user.MailUBLMessage;
import org.openfact.services.managers.BrokerManager;

import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Named
public class PullMailMessagesProcessor implements ItemProcessor {

    private static final Logger logger = Logger.getLogger(PullMailMessagesProcessor.class);

    @Override
    public PullMailMessageWrapper processItem(Object item) throws Exception {
        PullMailMessageWrapper wrapper;
        if (item instanceof PullMailMessageWrapper) {
            wrapper = (PullMailMessageWrapper) item;
        } else {
            logger.error("Could not cast to " + PullMailMessageWrapper.class.getName());
            throw new IllegalStateException("Could not cast to " + PullMailMessageWrapper.class.getName());
        }

        return wrapper;
    }

}
