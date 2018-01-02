package org.clarksnut.batchs.common;

import org.jboss.logging.Logger;

import javax.batch.api.AbstractBatchlet;
import javax.batch.runtime.BatchStatus;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

@Named
@Dependent
public class EndBatchlet extends AbstractBatchlet {

    private static final Logger logger = Logger.getLogger(EndBatchlet.class);

    @Override
    public String process() {
        logger.info("Ending batchlet...");
        return BatchStatus.COMPLETED.toString();
    }

}
