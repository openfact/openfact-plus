package org.openfact.services.managers;

import org.jboss.logging.Logger;
import org.openfact.OpenfactService;
import org.openfact.models.ModelException;
import org.openfact.models.OpenfactProvider;
import org.openfact.syncronization.SyncronizationModel;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.math.BigInteger;

@Startup
@Singleton
public class ScheduledTask {

    private final static Logger logger = Logger.getLogger(ScheduledTask.class);

    @Inject
    private OpenfactProvider openfactProvider;

    @Inject
    private OpenfactService openfactService;

    @Schedule(hour = "*", minute = "*/1", persistent = false)
    private void execute() {
        logger.info("Scheduled Task starting at " + System.currentTimeMillis());

        SyncronizationModel syncronizationModel = openfactProvider.getSyncronizationModel();
        BigInteger startHistoryId = syncronizationModel.getHistoryId();
        try {
            openfactService.synchronize(startHistoryId);
        } catch (ModelException e) {
            logger.error("Startup error, could not read messages");
        }

        logger.info("Scheduled Task ended at " + System.currentTimeMillis());
    }

}
