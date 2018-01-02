package org.clarksnut.batchs.support.utils;

import org.jboss.logging.Logger;

import javax.batch.api.listener.JobListener;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class JobLoggerListener extends AbstractLoggerListener implements JobListener {

    private static final Logger logger = Logger.getLogger(JobLoggerListener.class);

    @Inject
    private JobContext jobContext;

    @Override
    public void beforeJob() throws Exception {
        logger.log(getLevel(), "===============================================");
        logger.log(getLevel(), "Starting Job:" + jobContext.getJobName());
        logger.log(getLevel(), "Job init status:" + jobContext.getBatchStatus());
    }

    @Override
    public void afterJob() throws Exception {
        logger.log(getLevel(), "Job ended:" + jobContext.getJobName());
        logger.log(getLevel(), "Job exit status:" + jobContext.getExitStatus());
        logger.log(getLevel(), "===============================================");
    }

}
