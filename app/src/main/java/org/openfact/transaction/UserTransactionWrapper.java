package org.openfact.transaction;

import javax.transaction.Status;
import javax.transaction.UserTransaction;

public class UserTransactionWrapper implements OpenfactTransaction {

    protected UserTransaction ut;

    public UserTransactionWrapper(UserTransaction ut) {
        this.ut = ut;
    }

    @Override
    public void begin() {
        try {
            ut.begin();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit() {
        try {
            ut.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollback() {
        try {
            ut.rollback();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void setRollbackOnly() {
        try {
            ut.setRollbackOnly();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean getRollbackOnly() {
        try {
            return ut.getStatus() == Status.STATUS_MARKED_ROLLBACK;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isActive() {
        try {
            return ut.getStatus() == Status.STATUS_ACTIVE;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
