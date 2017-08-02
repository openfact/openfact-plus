package org.openfact.batchs.read;

import org.jboss.logging.Logger;
import org.openfact.batchs.BatchContants;

import javax.batch.runtime.BatchRuntime;
import java.util.Properties;

public class ReadMailJob implements Runnable {

    private static final Logger logger = Logger.getLogger(ReadMailJob.class);

    @Override
    public void run() {
        BatchRuntime.getJobOperator().start(BatchContants.READ_MAIL_JOB_NAME, new Properties());
    }

    protected void afterRun() {
        logger.info("ReadMailJob successfully executed...");
    }

}
