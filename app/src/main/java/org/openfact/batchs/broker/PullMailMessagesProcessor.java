package org.openfact.batchs.broker;

import org.jboss.logging.Logger;

import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Named;

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
