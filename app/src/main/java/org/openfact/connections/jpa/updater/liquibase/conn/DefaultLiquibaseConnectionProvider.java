package org.openfact.connections.jpa.updater.liquibase.conn;

import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.logging.LogFactory;
import liquibase.logging.LogLevel;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import liquibase.servicelocator.ServiceLocator;
import liquibase.sqlgenerator.SqlGeneratorFactory;
import org.jboss.logging.Logger;
import org.openfact.connections.jpa.updater.liquibase.LiquibaseJpaUpdaterProvider;
import org.openfact.connections.jpa.updater.liquibase.PostgresPlusDatabase;
import org.openfact.connections.jpa.updater.liquibase.lock.CustomInsertLockRecordGenerator;
import org.openfact.connections.jpa.updater.liquibase.lock.CustomLockDatabaseChangeLogGenerator;
import org.openfact.connections.jpa.updater.liquibase.lock.DummyLockService;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import java.sql.Connection;

@Singleton
public class DefaultLiquibaseConnectionProvider implements LiquibaseConnectionProvider {

    private static final Logger logger = Logger.getLogger(DefaultLiquibaseConnectionProvider.class);

    @PostConstruct
    public void baseLiquibaseInitialization() {
        ServiceLocator sl = ServiceLocator.getInstance();
        sl.setResourceAccessor(new ClassLoaderResourceAccessor(getClass().getClassLoader()));

        if (!System.getProperties().containsKey("liquibase.scan.packages")) {
            if (sl.getPackages().remove("liquibase.core")) {
                sl.addPackageToScan("liquibase.core.xml");
            }

            if (sl.getPackages().remove("liquibase.parser")) {
                sl.addPackageToScan("liquibase.parser.core.xml");
            }

            if (sl.getPackages().remove("liquibase.serializer")) {
                sl.addPackageToScan("liquibase.serializer.core.xml");
            }

            sl.getPackages().remove("liquibase.ext");
            sl.getPackages().remove("liquibase.sdk");

            String lockPackageName = DummyLockService.class.getPackage().getName();
            logger.debugf("Added package %s to liquibase", lockPackageName);
            sl.addPackageToScan(lockPackageName);
        }

        LogFactory.setInstance(new LogWrapper());

        // Adding PostgresPlus support to liquibase
        DatabaseFactory.getInstance().register(new PostgresPlusDatabase());

        // Change command for creating lock and drop DELETE lock record from it
        SqlGeneratorFactory.getInstance().register(new CustomInsertLockRecordGenerator());

        // Use "SELECT FOR UPDATE" for locking database
        SqlGeneratorFactory.getInstance().register(new CustomLockDatabaseChangeLogGenerator());
    }

    @Override
    public Liquibase getLiquibase(Connection connection, String defaultSchema) throws LiquibaseException {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        if (defaultSchema != null) {
            database.setDefaultSchemaName(defaultSchema);
        }

        String changelog = LiquibaseJpaUpdaterProvider.CHANGELOG;
        ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());

        logger.debugf("Using changelog file %s and changelogTableName %s", changelog, database.getDatabaseChangeLogTableName());

        return new Liquibase(changelog, resourceAccessor, database);
    }

    @Override
    public Liquibase getLiquibaseForCustomUpdate(Connection connection, String defaultSchema, String changelogLocation, ClassLoader classloader, String changelogTableName) throws LiquibaseException {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        if (defaultSchema != null) {
            database.setDefaultSchemaName(defaultSchema);
        }

        ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor(classloader);
        database.setDatabaseChangeLogTableName(changelogTableName);

        logger.debugf("Using changelog file %s and changelogTableName %s", changelogLocation, database.getDatabaseChangeLogTableName());

        return new Liquibase(changelogLocation, resourceAccessor, database);
    }

    private static class LogWrapper extends LogFactory {

        private static final liquibase.logging.Logger logger = new liquibase.logging.Logger() {
            @Override
            public void setName(String name) {
            }

            @Override
            public void setLogLevel(String level) {
            }

            @Override
            public void setLogLevel(LogLevel level) {
            }

            @Override
            public void setLogLevel(String logLevel, String logFile) {
            }

            @Override
            public void closeLogFile() {
            }

            @Override
            public void severe(String message) {
                DefaultLiquibaseConnectionProvider.logger.error(message);
            }

            @Override
            public void severe(String message, Throwable e) {
                DefaultLiquibaseConnectionProvider.logger.error(message, e);
            }

            @Override
            public void warning(String message) {
                // Ignore this warning as cascaded drops doesn't work anyway with all DBs, which we need to support
                if ("Database does not support drop with cascade".equals(message)) {
                    DefaultLiquibaseConnectionProvider.logger.debug(message);
                } else {
                    DefaultLiquibaseConnectionProvider.logger.warn(message);
                }
            }

            @Override
            public void warning(String message, Throwable e) {
                DefaultLiquibaseConnectionProvider.logger.warn(message, e);
            }

            @Override
            public void info(String message) {
                DefaultLiquibaseConnectionProvider.logger.debug(message);
            }

            @Override
            public void info(String message, Throwable e) {
                DefaultLiquibaseConnectionProvider.logger.debug(message, e);
            }

            @Override
            public void debug(String message) {
                if (DefaultLiquibaseConnectionProvider.logger.isTraceEnabled()) {
                    DefaultLiquibaseConnectionProvider.logger.trace(message);
                }
            }

            @Override
            public LogLevel getLogLevel() {
                if (DefaultLiquibaseConnectionProvider.logger.isTraceEnabled()) {
                    return LogLevel.DEBUG;
                } else if (DefaultLiquibaseConnectionProvider.logger.isDebugEnabled()) {
                    return LogLevel.INFO;
                } else {
                    return LogLevel.WARNING;
                }
            }

            @Override
            public void debug(String message, Throwable e) {
                DefaultLiquibaseConnectionProvider.logger.trace(message, e);
            }

            @Override
            public void setChangeLog(DatabaseChangeLog databaseChangeLog) {
            }

            @Override
            public void setChangeSet(ChangeSet changeSet) {
            }

            @Override
            public int getPriority() {
                return 0;
            }
        };

        @Override
        public liquibase.logging.Logger getLog(String name) {
            return logger;
        }

        @Override
        public liquibase.logging.Logger getLog() {
            return logger;
        }

    }
}
