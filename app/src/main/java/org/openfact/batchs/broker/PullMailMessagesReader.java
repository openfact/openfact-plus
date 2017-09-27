package org.openfact.batchs.broker;

import org.jberet.support.io.JpaItemReader;
import org.jboss.logging.Logger;
import org.openfact.models.DocumentProvider;
import org.openfact.models.db.jpa.entity.UserLinkedBrokerEntity;
import org.openfact.repositories.user.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Named
public class PullMailMessagesReader extends JpaItemReader {

    private static final Logger logger = Logger.getLogger(PullMailMessagesReader.class);

    @Inject
    private MailUtils mailUtils;

    @Inject
    private DocumentProvider documentProvider;

    protected List<PullMailMessageWrapper> messageList = new ArrayList<>();

    @Override
    public void open(final Serializable checkpoint) throws Exception {
        super.open(checkpoint);
        for (Object o : super.resultList) {
            UserLinkedBrokerEntity linkedBroker = (UserLinkedBrokerEntity) o;
            MailProvider mailProvider = mailUtils.getMailReader(linkedBroker.getType());
            if (mailProvider != null) {
                MailRepositoryModel repository = MailRepositoryModel.builder()
                        .email(linkedBroker.getEmail())
                        .refreshToken(linkedBroker.getUser().getOfflineToken())
                        .build();

                MailQuery.Builder queryBuilder = MailQuery.builder().fileType("xml");
                LocalDateTime lastTimeSynchronized = linkedBroker.getLastTimeSynchronized();
                if (lastTimeSynchronized != null) {
                    queryBuilder.after(lastTimeSynchronized);
                }

                for (MailUblMessageModel message : mailProvider.getUblMessages(repository, queryBuilder.build())) {
                    if (documentProvider.isSupported(message.getXml())) {
                        this.messageList.add(new PullMailMessageWrapper(message));
                    }
                }
            } else {
                logger.warn("Skipping broker getUblMessages");
            }
        }
    }

    @Override
    public Object readItem() throws Exception {
        if (readPosition >= this.messageList.size()) {
            return null;
        }
        return this.messageList.get(readPosition++);
    }

}
