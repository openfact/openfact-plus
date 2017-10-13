package org.openfact.batchs.support.utils;

import org.jboss.logging.Logger;

import javax.batch.api.listener.JobListener;
import javax.inject.Named;

@Named
public class BatchLoggerListener implements JobListener {

    private static final Logger logger = Logger.getLogger(BatchLoggerListener.class);

    @Override
    public void beforeJob() throws Exception {
        logger.infof("Starting job...");
    }

    @Override
    public void afterJob() throws Exception {
        logger.infof("Ending job...");
    }

}
