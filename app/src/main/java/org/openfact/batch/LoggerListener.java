package org.openfact.batch;

import org.jboss.logging.Logger;
import org.openfact.services.resources.DocumentsService;

import javax.batch.api.listener.JobListener;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@Dependent
public class LoggerListener implements JobListener {

    private static final Logger logger = Logger.getLogger(DocumentsService.class);

    @Inject
    private StepContext stepContext;

    @Override
    public void beforeJob() throws Exception {
        logger.infof("Step: {}", stepContext.getStepName());
    }

    @Override
    public void afterJob() throws Exception {
        logger.infof("Step: {}", stepContext.getExitStatus());
    }

}
