package org.openfact.transaction;

import org.jboss.logging.Logger;
import org.openfact.models.OpenfactSession;
import org.openfact.transaction.jboss.JBossJtaTransactionManagerLookup;

import javax.transaction.TransactionManager;
import java.util.LinkedList;
import java.util.List;

public class DefaultOpenfactTransactionManager implements OpenfactTransactionManager {

    private static final Logger logger = Logger.getLogger(DefaultOpenfactTransactionManager.class);

    private List<OpenfactTransaction> prepare = new LinkedList<OpenfactTransaction>();
    private List<OpenfactTransaction> transactions = new LinkedList<OpenfactTransaction>();
    private List<OpenfactTransaction> afterCompletion = new LinkedList<OpenfactTransaction>();
    private boolean active;
    private boolean rollback;
    private OpenfactSession session;
    private JTAPolicy jtaPolicy = JTAPolicy.REQUIRES_NEW;

    public DefaultOpenfactTransactionManager(OpenfactSession session) {
        this.session = session;
    }

    @Override
    public void enlist(OpenfactTransaction transaction) {
        if (active && !transaction.isActive()) {
            transaction.begin();
        }

        transactions.add(transaction);
    }

    @Override
    public void enlistAfterCompletion(OpenfactTransaction transaction) {
        if (active && !transaction.isActive()) {
            transaction.begin();
        }

        afterCompletion.add(transaction);
    }

    @Override
    public void enlistPrepare(OpenfactTransaction transaction) {
        if (active && !transaction.isActive()) {
            transaction.begin();
        }

        prepare.add(transaction);
    }

    @Override
    public JTAPolicy getJTAPolicy() {
        return jtaPolicy;
    }

    @Override
    public void setJTAPolicy(JTAPolicy policy) {
        jtaPolicy = policy;

    }

    @Override
    public void begin() {
//        if (active) {
//            throw new IllegalStateException("Transaction already active");
//        }
//
//        if (jtaPolicy == JTAPolicy.REQUIRES_NEW) {
//            JtaTransactionManagerLookup jtaLookup = session.getProvider(JtaTransactionManagerLookup.class);
//            if (jtaLookup != null) {
//                TransactionManager tm = jtaLookup.getTransactionManager();
//                if (tm != null) {
//                    enlist(new JtaTransactionWrapper(session.getOpenfactSessionFactory(), tm));
//                }
//            }
//        }
//
//        for (OpenfactTransaction tx : transactions) {
//            tx.begin();
//        }
//
//        active = true;
    }

    @Override
    public void commit() {
        RuntimeException exception = null;
        for (OpenfactTransaction tx : prepare) {
            try {
                tx.commit();
            } catch (RuntimeException e) {
                exception = exception == null ? e : exception;
            }
        }
        if (exception != null) {
            rollback(exception);
            return;
        }
        for (OpenfactTransaction tx : transactions) {
            try {
                tx.commit();
            } catch (RuntimeException e) {
                exception = exception == null ? e : exception;
            }
        }

        // Don't commit "afterCompletion" if commit of some main transaction failed
        if (exception == null) {
            for (OpenfactTransaction tx : afterCompletion) {
                try {
                    tx.commit();
                } catch (RuntimeException e) {
                    exception = exception == null ? e : exception;
                }
            }
        } else {
            for (OpenfactTransaction tx : afterCompletion) {
                try {
                    tx.rollback();
                } catch (RuntimeException e) {
                    logger.error("Exception during rollback");
                }
            }
        }

        active = false;
        if (exception != null) {
            throw exception;
        }
    }

    @Override
    public void rollback() {
        RuntimeException exception = null;
        rollback(exception);
    }

    protected void rollback(RuntimeException exception) {
        for (OpenfactTransaction tx : transactions) {
            try {
                tx.rollback();
            } catch (RuntimeException e) {
                exception = exception != null ? e : exception;
            }
        }
        for (OpenfactTransaction tx : afterCompletion) {
            try {
                tx.rollback();
            } catch (RuntimeException e) {
                exception = exception != null ? e : exception;
            }
        }
        active = false;
        if (exception != null) {
            throw exception;
        }
    }

    @Override
    public void setRollbackOnly() {
        rollback = true;
    }

    @Override
    public boolean getRollbackOnly() {
        if (rollback) {
            return true;
        }

        for (OpenfactTransaction tx : transactions) {
            if (tx.getRollbackOnly()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isActive() {
        return active;
    }

}
