package org.openfact.transaction;

import org.jboss.logging.Logger;

public abstract class AbstractOpenfactTransaction implements OpenfactTransaction {

    public static final Logger logger = Logger.getLogger(AbstractOpenfactTransaction.class);

    protected TransactionState state = TransactionState.NOT_STARTED;

    @Override
    public void begin() {
        if (state != TransactionState.NOT_STARTED) {
            throw new IllegalStateException("Transaction already started");
        }

        state = TransactionState.STARTED;
    }

    @Override
    public void commit() {
        if (state != TransactionState.STARTED) {
            throw new IllegalStateException("Transaction in illegal state for commit: " + state);
        }

        commitImpl();

        state = TransactionState.FINISHED;
    }

    @Override
    public void rollback() {
        if (state != TransactionState.STARTED && state != TransactionState.ROLLBACK_ONLY) {
            throw new IllegalStateException("Transaction in illegal state for rollback: " + state);
        }

        rollbackImpl();

        state = TransactionState.FINISHED;
    }

    @Override
    public void setRollbackOnly() {
        state = TransactionState.ROLLBACK_ONLY;
    }

    @Override
    public boolean getRollbackOnly() {
        return state == TransactionState.ROLLBACK_ONLY;
    }

    @Override
    public boolean isActive() {
        return state == TransactionState.STARTED || state == TransactionState.ROLLBACK_ONLY;
    }

    public TransactionState getState() {
        return state;
    }

    public enum TransactionState {
        NOT_STARTED, STARTED, ROLLBACK_ONLY, FINISHED
    }


    protected abstract void commitImpl();

    protected abstract void rollbackImpl();

}
