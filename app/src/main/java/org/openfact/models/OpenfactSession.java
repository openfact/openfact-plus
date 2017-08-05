package org.openfact.models;

import org.openfact.transaction.OpenfactTransactionManager;

public interface OpenfactSession {

    OpenfactTransactionManager getTransactionManager();

    OpenfactSessionFactory getOpenfactSessionFactory();

    void close();

}
