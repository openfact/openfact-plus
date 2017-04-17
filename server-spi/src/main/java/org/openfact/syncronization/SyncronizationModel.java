package org.openfact.syncronization;

import java.math.BigInteger;

public interface SyncronizationModel {

    BigInteger getHistoryId();

    void setHistoryId(BigInteger historyId);

}
