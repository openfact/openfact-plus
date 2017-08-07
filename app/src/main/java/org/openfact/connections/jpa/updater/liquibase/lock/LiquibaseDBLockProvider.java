package org.openfact.connections.jpa.updater.liquibase.lock;

import liquibase.Liquibase;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import org.jboss.logging.Logger;
import org.openfact.connections.jpa.JpaConnectionProviderFactory;
import org.openfact.connections.jpa.updater.liquibase.conn.LiquibaseConnectionProvider;
import org.openfact.models.dblock.DBLockProvider;

import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class LiquibaseDBLockProvider implements DBLockProvider {

    private static final Logger logger = Logger.getLogger(LiquibaseDBLockProvider.class);

    // 3 should be sufficient (Potentially one failure for createTable and one for insert record)
    private final static int DEFAULT_MAX_ATTEMPTS = 3;

    // True if this node has a lock acquired
    private final AtomicBoolean hasLock = new AtomicBoolean(false);

    @Inject
    private JpaConnectionProviderFactory jpaProviderFactory;

    @Inject
    private LiquibaseConnectionProvider liquibaseProvider;

    private CustomLockService lazyInit(Connection dbConnection, String defaultSchema) {
        CustomLockService lockService;
        try {
            Liquibase liquibase = liquibaseProvider.getLiquibase(dbConnection, defaultSchema);

            lockService = new CustomLockService();
            lockService.setChangeLogLockWaitTime(((long) 900) * 1000);
            lockService.setDatabase(liquibase.getDatabase());
            return lockService;
        } catch (LiquibaseException exception) {
            safeRollbackConnection(dbConnection);
            safeCloseConnection(dbConnection);
            throw new IllegalStateException(exception);
        }
    }

    // Assumed transaction was rolled-back and we want to start with new DB connection
    private CustomLockService restart(Connection dbConnection, String defaultSchema) {
        safeCloseConnection(dbConnection);
        dbConnection = jpaProviderFactory.getConnection();
        return lazyInit(dbConnection, defaultSchema);
    }

    @Override
    public void waitForLock() {
        Connection dbConnection = jpaProviderFactory.getConnection();
        String defaultSchema = jpaProviderFactory.getSchema();

        CustomLockService lockService = lazyInit(dbConnection, defaultSchema);

        int maxAttempts = DEFAULT_MAX_ATTEMPTS;
        while (maxAttempts > 0) {
            try {
                lockService.waitForLock();
                hasLock.set(true);
                maxAttempts = DEFAULT_MAX_ATTEMPTS;
                return;
            } catch (LockRetryException le) {
                // Indicates we should try to acquire lock again in different transaction
                safeRollbackConnection(dbConnection);
                lockService = restart(dbConnection, defaultSchema);
                maxAttempts--;
            } catch (RuntimeException re) {
                safeRollbackConnection(dbConnection);
                safeCloseConnection(dbConnection);
                throw re;
            }
        }
    }


    @Override
    public void releaseLock() {
        Connection dbConnection = jpaProviderFactory.getConnection();
        String defaultSchema = jpaProviderFactory.getSchema();

        CustomLockService lockService = lazyInit(dbConnection, defaultSchema);

        lockService.releaseLock();
        lockService.reset();
        setHasLock(false);
    }

    @Override
    public boolean hasLock() {
        return this.hasLock.get();
    }

    private void setHasLock(boolean hasLock) {
        this.hasLock.set(hasLock);
    }

    @Override
    public boolean supportsForcedUnlock() {
        // Implementation based on "SELECT FOR UPDATE" can't force unlock as it's locked by other transaction
        return false;
    }

    @Override
    public void destroyLockInfo() {
        Connection dbConnection = jpaProviderFactory.getConnection();
        String defaultSchema = jpaProviderFactory.getSchema();

        CustomLockService lockService = lazyInit(dbConnection, defaultSchema);

        try {
            lockService.destroy();
            dbConnection.commit();
            logger.debug("Destroyed lock table");
        } catch (DatabaseException | SQLException de) {
            logger.error("Failed to destroy lock table");
            safeRollbackConnection(dbConnection);
        }
    }

    private void safeRollbackConnection(Connection dbConnection) {
        if (dbConnection != null) {
            try {
                dbConnection.rollback();
            } catch (SQLException se) {
                logger.warn("Failed to rollback connection after error", se);
            }
        }
    }

    private void safeCloseConnection(Connection dbConnection) {
        // Close to prevent in-mem databases from closing
        if (dbConnection != null) {
            try {
                dbConnection.close();
            } catch (SQLException e) {
                logger.warn("Failed to close connection", e);
            }
        }
    }
}
