package org.clarksnut.batchs.broker;

import org.clarksnut.models.UserLinkedBrokerModel;

import javax.annotation.Resource;
import javax.batch.api.listener.StepListener;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.UserTransaction;
import java.time.LocalDateTime;
import java.util.Map;

@Named
public class PullMailMessagesStepListener implements StepListener {

    @Inject
    private LinkedBrokers linkedBrokers;

    @Resource
    private UserTransaction utx;

    @Override
    public void beforeStep() throws Exception {

    }

    @Override
    public void afterStep() throws Exception {
        utx.begin();
        for (Map.Entry<UserLinkedBrokerModel, LocalDateTime> entry : linkedBrokers.getLinkedBrokers().entrySet()) {
            UserLinkedBrokerModel userLinkedBroker = entry.getKey();
            userLinkedBroker.setLasTimeSynchronized(entry.getValue());
        }
        utx.commit();
    }
}
