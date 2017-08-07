package org.openfact.transaction;

import org.jboss.logging.Logger;
import org.openfact.models.OpenfactSessionFactory;
import org.openfact.provider.ExceptionConverter;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

public class JtaTransactionWrapper implements OpenfactTransaction {

    private static final Logger logger = Logger.getLogger(JtaTransactionWrapper.class);
    protected TransactionManager tm;
    protected Transaction ut;
    protected Transaction suspended;
    protected Exception ended;
    protected OpenfactSessionFactory factory;

    @Inject
    @Any
    private Instance<ExceptionConverter> exceptionConverter;

//    public JtaTransactionWrapper(OpenfactSessionFactory factory, TransactionManager tm) {
//        this.tm = tm;
//        this.factory = factory;
//        try {
//
//            suspended = tm.suspend();
//            logger.debug("new JtaTransactionWrapper");
//            logger.debugv("was existing? {0}", suspended != null);
//            tm.begin();
//            ut = tm.getTransaction();
//            //ended = new Exception();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void handleException(Throwable e) {
        if (e instanceof RollbackException) {
            e = e.getCause() != null ? e.getCause() : e;
        }

        for (ExceptionConverter converter : exceptionConverter) {
            Throwable throwable = converter.convert(e);
            if (throwable == null) continue;
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException)throwable;
            } else {
                throw new RuntimeException(throwable);
            }
        }

        if (e instanceof RuntimeException) {
            throw (RuntimeException)e;
        } else {
            throw new RuntimeException(e);
        }



    }

    @Override
    public void begin() {
    }

    @Override
    public void commit() {
        try {
            logger.debug("JtaTransactionWrapper  commit");
            tm.commit();
        } catch (Exception e) {
            handleException(e);
        } finally {
            end();
        }
    }

    @Override
    public void rollback() {
        try {
            logger.debug("JtaTransactionWrapper rollback");
            tm.rollback();
        } catch (Exception e) {
            handleException(e);
        } finally {
            end();
        }

    }

    @Override
    public void setRollbackOnly() {
        try {
            tm.setRollbackOnly();
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public boolean getRollbackOnly() {
        try {
            return tm.getStatus() == Status.STATUS_MARKED_ROLLBACK;
        } catch (Exception e) {
            handleException(e);
        }
        return false;
    }

    @Override
    public boolean isActive() {
        try {
            return tm.getStatus() == Status.STATUS_ACTIVE;
        } catch (Exception e) {
            handleException(e);
        }
        return false;
    }
    /*

    @Override
    protected void finalize() throws Throwable {
        if (ended != null) {
            logger.error("TX didn't close at position", ended);
        }

    }
    */

    protected void end() {
        ended = null;
        logger.debug("JtaTransactionWrapper end");
        if (suspended != null) {
            try {
                logger.debug("JtaTransactionWrapper resuming suspended");
                tm.resume(suspended);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}
