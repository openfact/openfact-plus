package org.openfact.transaction;

import javax.transaction.TransactionManager;

/**
 * JTA TransactionManager lookup
 */
public interface JtaTransactionManagerLookup {

    TransactionManager getTransactionManager();

}
