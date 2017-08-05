package org.openfact.connections.jpa.updater.liquibase.lock;

import liquibase.Liquibase;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import org.jboss.logging.Logger;
import org.keycloak.common.util.Time;
import org.openfact.models.dblock.DBLockProvider;
import org.openfact.models.utils.OpenfactModelUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
public class LiquibaseDBLockProvider implements DBLockProvider {

    private static final Logger logger = Logger.getLogger(LiquibaseDBLockProvider.class);

    // 3 should be sufficient (Potentially one failure for createTable and one for insert record)
    private int DEFAULT_MAX_ATTEMPTS = 3;


    private CustomLockService lockService;
    private Connection dbConnection;
    private boolean initialized = false;

    private int maxAttempts = DEFAULT_MAX_ATTEMPTS;

    // FACTORY
    private long lockWaitTimeoutMillis;

    // True if this node has a lock acquired
    private AtomicBoolean hasLock = new AtomicBoolean(false);

    @PostConstruct
    private void init() {
        int lockWaitTimeout = 900;
        this.lockWaitTimeoutMillis = Time.toMillis(lockWaitTimeout);
        logger.debugf("Liquibase lock provider configured with lockWaitTime: %d seconds", lockWaitTimeout);
    }

    @PreDestroy
    private void close() {
        OpenfactModelUtils.suspendJtaTransaction(session.getKeycloakSessionFactory(), () -> {
            safeCloseConnection();
        });
    }

    protected long getLockWaitTimeoutMillis() {
        return lockWaitTimeoutMillis;
    }

    public void setTimeouts(long lockRecheckTimeMillis, long lockWaitTimeoutMillis) {
        this.lockWaitTimeoutMillis = lockWaitTimeoutMillis;
    }

    // PROVIDER

    private void lazyInit() {
        if (!initialized) {
            LiquibaseConnectionProvider liquibaseProvider = session.getProvider(LiquibaseConnectionProvider.class);
            JpaConnectionProviderFactory jpaProviderFactory = (JpaConnectionProviderFactory) session.getKeycloakSessionFactory().getProviderFactory(JpaConnectionProvider.class);

            this.dbConnection = jpaProviderFactory.getConnection();
            String defaultSchema = jpaProviderFactory.getSchema();

            try {
                Liquibase liquibase = liquibaseProvider.getLiquibase(dbConnection, defaultSchema);

                this.lockService = new CustomLockService();
                lockService.setChangeLogLockWaitTime(factory.getLockWaitTimeoutMillis());
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
        OpenfactModelUtils.suspendJtaTransaction(session.getKeycloakSessionFactory(), () -> {

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
        });

    }


    @Override
    public void releaseLock() {
        OpenfactModelUtils.suspendJtaTransaction(session.getKeycloakSessionFactory(), () -> {
            lazyInit();

            lockService.releaseLock();
            lockService.reset();
            factory.setHasLock(false);
        });
    }

    @Override
    public boolean hasLock() {
        return hasLock.get();
    }

    @Override
    public boolean supportsForcedUnlock() {
        // Implementation based on "SELECT FOR UPDATE" can't force unlock as it's locked by other transaction
        return false;
    }

    @Override
    public void destroyLockInfo() {
        OpenfactModelUtils.suspendJtaTransaction(session.getKeycloakSessionFactory(), () -> {
            lazyInit();

            try {
                this.lockService.destroy();
                dbConnection.commit();
                logger.debug("Destroyed lock table");
            } catch (DatabaseException | SQLException de) {
                logger.error("Failed to destroy lock table");
                safeRollbackConnection();
            }
        });
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
