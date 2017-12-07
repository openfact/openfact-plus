package org.clarksnut.batchs.support.utils;

import org.jboss.logging.Logger;

import javax.batch.api.listener.StepListener;
import javax.batch.runtime.Metric;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class StepLoggerListener extends AbstractLoggerListener implements StepListener {

    private static final Logger logger = Logger.getLogger(StepLoggerListener.class);

    @Inject
    private StepContext stepContext;

    @Override
    public void beforeStep() throws Exception {
        logger.log(getLevel(), "Starting Step:" + stepContext.getStepName());
        logger.log(getLevel(), "Step init status:" + stepContext.getBatchStatus());
    }

    @Override
    public void afterStep() throws Exception {
        logger.log(getLevel(), "Step ended:" + stepContext.getStepName());
        logger.log(getLevel(), "Step exit status:" + stepContext.getExitStatus());

        logger.log(getLevel(), "___________________________");
        for (Metric metric : stepContext.getMetrics()) {
            logger.log(getLevel(), metric.getType() + ":" + metric.getValue());
        }
        logger.log(getLevel(), "___________________________");
    }
}
