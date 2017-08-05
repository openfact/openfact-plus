package org.openfact.transaction;

public interface OpenfactTransaction {
    void begin();
    void commit();
    void rollback();
    void setRollbackOnly();
    boolean getRollbackOnly();
    boolean isActive();
}
