package org.openfact.connections.jpa.updater.liquibase.lock;

import liquibase.Liquibase;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import org.jboss.logging.Logger;
import org.openfact.connections.jpa.JpaConnectionProviderFactory;
import org.openfact.connections.jpa.updater.liquibase.conn.LiquibaseConnectionProvider;
import org.openfact.models.dblock.DBLockProvider;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.annotation.PreDestroy;
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

    // True if this node has a lock acquired
    private AtomicBoolean hasLock = new AtomicBoolean(false);

    // 3 should be sufficient (Potentially one failure for createTable and one for insert record)
    private int DEFAULT_MAX_ATTEMPTS = 3;

    private CustomLockService lockService;
    private boolean initialized = false;

    private int maxAttempts = DEFAULT_MAX_ATTEMPTS;

    @Inject
    private Connection connection;

    @Inject
    private LiquibaseConnectionProvider liquibaseProvider;

    private void lazyInit() {
        if (!initialized) {
            JpaConnectionProviderFactory jpaProviderFactory = (JpaConnectionProviderFactory) session.getKeycloakSessionFactory().getProviderFactory(JpaConnectionProvider.class);

            try {
                Liquibase liquibase = liquibaseProvider.getLiquibase(dbConnection, defaultSchema);

                this.lockService = new CustomLockService();
                lockService.setChangeLogLockWaitTime(((long) 900) * 1000);
                lockService.setDatabase(liquibase.getDatabase());
                initialized = true;
            } catch (LiquibaseException exception) {
                safeRollbackConnection();
                safeCloseConnection();
                throw new IllegalStateException(exception);
            }
        }
    }

    // Assumed transaction was rolled-back and we want to start with new DB connection
    private void restart() {
        safeCloseConnection();
        this.dbConnection = null;
        this.lockService = null;
        initialized = false;
        lazyInit();
    }


    @Override
    public void waitForLock() {
        lazyInit();

        while (maxAttempts > 0) {
            try {
                lockService.waitForLock();
                hasLock.set(true);
                this.maxAttempts = DEFAULT_MAX_ATTEMPTS;
                return;
            } catch (LockRetryException le) {
                // Indicates we should try to acquire lock again in different transaction
                safeRollbackConnection();
                restart();
                maxAttempts--;
            } catch (RuntimeException re) {
                safeRollbackConnection();
                safeCloseConnection();
                throw re;
            }
        }
    }


    @Override
    public void releaseLock() {
        lazyInit();

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
        lazyInit();

        try {
            this.lockService.destroy();
            dbConnection.commit();
            logger.debug("Destroyed lock table");
        } catch (DatabaseException | SQLException de) {
            logger.error("Failed to destroy lock table");
            safeRollbackConnection();
        }
    }

    private void safeRollbackConnection() {
        if (dbConnection != null) {
            try {
                this.dbConnection.rollback();
            } catch (SQLException se) {
                logger.warn("Failed to rollback connection after error", se);
            }
        }
    }

    private void safeCloseConnection() {
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
