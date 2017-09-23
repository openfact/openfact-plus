package org.openfact.batchs.broker;

import org.jberet.support.io.JpaItemReader;
import org.jboss.logging.Logger;
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

    protected List<PullMailMessageWrapper> messageList = new ArrayList<>();

    @Override
    public void open(final Serializable checkpoint) throws Exception {
        super.open(checkpoint);
        for (Object o : super.resultList) {
            UserLinkedBrokerEntity linkedBrokerEntity;
            if (o instanceof UserLinkedBrokerEntity) {
                linkedBrokerEntity = (UserLinkedBrokerEntity) o;
            } else {
                throw new IllegalStateException("Could not cast to " + UserLinkedBrokerEntity.class.getName() + " class");
            }

            MailReader reader = mailUtils.getMailReader(linkedBrokerEntity.getType());
            if (reader != null) {
                MailRepository mailRepository = MailRepository.builder()
                        .email(linkedBrokerEntity.getEmail())
                        .refreshToken(null)
                        .build();

                MailQuery.Builder mailQueryBuilder = MailQuery.builder();
                LocalDateTime currentTime = LocalDateTime.now();
                LocalDateTime lastTimeSynchronized = linkedBrokerEntity.getLastTimeSynchronized();
                mailQueryBuilder.to(currentTime);
                if (lastTimeSynchronized != null) {
                    mailQueryBuilder.from(lastTimeSynchronized);
                }

                for (MailUBLMessage message : reader.read(mailRepository, mailQueryBuilder.build())) {
                    this.messageList.add(new PullMailMessageWrapper(message));
                }
            } else {
                logger.warn("Skipping broker read");
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
