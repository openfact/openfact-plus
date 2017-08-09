package org.openfact;

import org.jboss.logging.Logger;
import org.openfact.migration.MigrationModelManager;
import org.openfact.models.MigrationBootstraper;
import org.openfact.models.dblock.DBLockManager;
import org.openfact.models.dblock.DBLockProvider;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@Startup
@Singleton
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ServerBootstraper {

    private static final Logger logger = Logger.getLogger(ServerBootstraper.class);

    @Inject
    private DBLockManager dbLockManager;

    @Inject
    private DBLockProvider dbLock;

    @Inject
    private MigrationModelManager migrationManager;

    @Inject
    private MigrationBootstraper migrationBootstraper;

    @PostConstruct
    private void init() {
        logger.info("====================================");
        logger.info("Locking database...");

        dbLockManager.checkForcedUnlock();
        dbLock.waitForLock();

        logger.info("Database locked");
        logger.info("====================================");


        logger.info("====================================");
        logger.info("Init migration (Liquibase)...");

        migrationBootstraper.init();

        logger.info("Migration Complete...");
        logger.info("====================================");

        try {
            migrateAndBootstrap();
        } finally {
            dbLock.releaseLock();
        }
    }

    // Migrate model, bootstrap server. This is done with acquired dbLock
    private void migrateAndBootstrap() {
        logger.debug("Calling migrateModel");
        migrateModel();

        logger.debug("bootstrap");
    }

    private void migrateModel() {
        migrationManager.migrate();
    }

}
