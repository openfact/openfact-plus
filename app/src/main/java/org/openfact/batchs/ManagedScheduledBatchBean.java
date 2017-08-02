package org.openfact.batchs;

import javax.ejb.Local;
import javax.ejb.Stateless;

@Stateless
@Local(ManagedScheduledBatch.class)
public class ManagedScheduledBatchBean implements ManagedScheduledBatch {

    @Override
    public void runJob() {

    }
}
