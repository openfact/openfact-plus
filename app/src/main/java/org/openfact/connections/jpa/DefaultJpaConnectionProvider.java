package org.openfact.connections.jpa;

import org.jboss.logging.Logger;
import org.openfact.ServerStartupError;
import org.openfact.connections.jpa.updater.JpaUpdaterProvider;
import org.openfact.models.dblock.DBLockProvider;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

@Singleton
@Startup
@TransactionManagement(TransactionManagementType.BEAN)
public class DefaultJpaConnectionProvider {

    private static final Logger logger = Logger.getLogger(DefaultJpaConnectionProvider.class);

    enum MigrationStrategy {
        UPDATE, VALIDATE, MANUAL
    }

    @Resource
    private DataSource ds;

    @Inject
    private JpaUpdaterProvider updater;

    @Inject
    private DBLockProvider dbLock;

    @PostConstruct
    private void init() {
        logger.trace("Create JpaConnectionProvider");
        lazyInit();
    }

    private void lazyInit() {
        logger.debug("Initializing JPA connections");

        String schema = getSchema();

        MigrationStrategy migrationStrategy = getMigrationStrategy();
        boolean initializeEmpty = true;
        File databaseUpdateFile = getDatabaseUpdateFile();

        Connection connection = getConnection();
        try {
            migration(migrationStrategy, initializeEmpty, schema, databaseUpdateFile, connection);
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

    private String getSchema() {
        return null;
    }

    private Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    private MigrationStrategy getMigrationStrategy() {
        return MigrationStrategy.UPDATE;
    }

    private File getDatabaseUpdateFile() {
        String databaseUpdateFile = "sync-database-update.sql";
        return new File(databaseUpdateFile);
    }

}
