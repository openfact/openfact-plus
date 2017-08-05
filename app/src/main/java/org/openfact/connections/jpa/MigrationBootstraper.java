package org.openfact.connections.jpa;

import org.jboss.logging.Logger;
import org.openfact.ServerStartupError;
import org.openfact.connections.jpa.updater.JpaUpdaterProvider;
import org.openfact.models.dblock.DBLockProvider;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

@Singleton
@Startup
@TransactionManagement(TransactionManagementType.BEAN)
public class MigrationBootstraper {

    private static final Logger logger = Logger.getLogger(MigrationBootstraper.class);

    enum MigrationStrategy {
        UPDATE, VALIDATE, MANUAL
    }

    @Inject
    private JpaUpdaterProvider updater;

    @Inject
    private DBLockProvider dbLock;

    @Inject
    private Connection connection;

    @PostConstruct
    private void init() {
        MigrationStrategy migrationStrategy = MigrationStrategy.UPDATE;
        File databaseUpdateFile = new File("openfact-database-update.sql");

        try {
            migration(migrationStrategy, true, null, databaseUpdateFile, connection);
        } finally {
            // Close after creating EntityManagerFactory to prevent in-mem databases from closing
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.warn("Can't close connection", e);
                }
            }
        }
    }

    private void migration(MigrationStrategy strategy, boolean initializeEmpty, String schema, File databaseUpdateFile, Connection connection) {
        JpaUpdaterProvider.Status status = updater.validate(connection, schema);
        if (status == JpaUpdaterProvider.Status.VALID) {
            logger.debug("Database is up-to-date");
        } else if (status == JpaUpdaterProvider.Status.EMPTY) {
            if (initializeEmpty) {
                update(connection, schema, updater);
            } else {
                switch (strategy) {
                    case UPDATE:
                        update(connection, schema, updater);
                        break;
                    case MANUAL:
                        export(connection, schema, databaseUpdateFile, updater);
                        throw new ServerStartupError("Database not initialized, please initialize database with " + databaseUpdateFile.getAbsolutePath(), false);
                    case VALIDATE:
                        throw new ServerStartupError("Database not initialized, please enable database initialization", false);
                }
            }
        } else {
            switch (strategy) {
                case UPDATE:
                    update(connection, schema, updater);
                    break;
                case MANUAL:
                    export(connection, schema, databaseUpdateFile, updater);
                    throw new ServerStartupError("Database not up-to-date, please migrate database with " + databaseUpdateFile.getAbsolutePath(), false);
                case VALIDATE:
                    throw new ServerStartupError("Database not up-to-date, please enable database migration", false);
            }
        }
    }

    protected void update(Connection connection, String schema, JpaUpdaterProvider updater) {
        if (dbLock.hasLock()) {
            updater.update(connection, schema);
        } else {
            OpenfactModelUtils.runJobInTransaction(() -> {
                dbLock.waitForLock();
                try {
                    updater.update(connection, schema);
                } finally {
                    dbLock.releaseLock();
                }
            });
        }
    }

    protected void export(Connection connection, String schema, File databaseUpdateFile, JpaUpdaterProvider updater) {
        if (dbLock.hasLock()) {
            updater.export(connection, schema, databaseUpdateFile);
        } else {
            OpenfactModelUtils.runJobInTransaction(() -> {
                dbLock.waitForLock();
                try {
                    updater.export(connection, schema, databaseUpdateFile);
                } finally {
                    dbLock.releaseLock();
                }
            });
        }
    }

}
