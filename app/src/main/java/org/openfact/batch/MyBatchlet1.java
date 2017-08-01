package org.openfact.batch;

import javax.batch.api.AbstractBatchlet;
import javax.batch.runtime.BatchStatus;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

@Named
@Dependent
public class MyBatchlet1 extends AbstractBatchlet {

    @Override
    public String process() {
        System.out.println("Running inside a batchlet 1");
        return BatchStatus.COMPLETED.toString();
    }

}
