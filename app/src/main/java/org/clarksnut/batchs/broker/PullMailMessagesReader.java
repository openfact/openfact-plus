package org.clarksnut.batchs.broker;

import org.clarksnut.models.UserLinkedBrokerModel;
import org.clarksnut.models.UserModel;
import org.clarksnut.models.UserProvider;
import org.clarksnut.repositories.user.*;
import org.jboss.logging.Logger;

import javax.batch.api.chunk.ItemReader;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
public class PullMailMessagesReader implements ItemReader {

    private static final Logger logger = Logger.getLogger(PullMailMessagesReader.class);

    @Inject
    private MailUtils mailUtils;

    @Inject
    private LinkedBrokers linkedBrokers;

    @Inject
    private UserProvider userProvider;

    /**
     * List to hold query result objects
     */
    protected List<MailUblMessageModel> resultList = new CopyOnWriteArrayList<>();

    /**
     * Current read position
     */
    protected int readPosition;

    private MailRepositoryModel buildRepository(UserLinkedBrokerModel userLinkedBroker) {
        return MailRepositoryModel.builder()
                .email(userLinkedBroker.getEmail())
                .refreshToken(userLinkedBroker.getUser().getOfflineRefreshToken())
                .build();
    }

    private MailQuery buildQuery(UserLinkedBrokerModel userLinkedBroker) {
        MailQuery.Builder queryBuilder = MailQuery.builder().fileType("xml");
        LocalDateTime lastTimeSynchronized = userLinkedBroker.getLastTimeSynchronized();
        if (lastTimeSynchronized != null) {
            queryBuilder.after(lastTimeSynchronized);
        }
        return queryBuilder.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void open(final Serializable checkpoint) throws Exception {
        List<UserModel> users = userProvider.getUsersWithOfflineToken();
        for (UserModel user : users) {
            for (UserLinkedBrokerModel userLinkedBroker : user.getLinkedBrokers()) {
                MailProvider mailProvider = mailUtils.getMailReader(userLinkedBroker.getType());
                if (mailProvider != null) {
                    MailRepositoryModel repository = buildRepository(userLinkedBroker);
                    MailQuery query = buildQuery(userLinkedBroker);
                    resultList.addAll(mailProvider.getUblMessages(repository, query));

                    linkedBrokers.add(userLinkedBroker, LocalDateTime.now());
                }
            }
        }

        if (checkpoint == null) {
            readPosition = 0;
        } else {
            readPosition = (Integer) checkpoint;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws Exception {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readItem() throws Exception {
        if (readPosition >= resultList.size()) {
            return null;
        }
        return resultList.get(readPosition++);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable checkpointInfo() throws Exception {
        return readPosition;
    }

}
