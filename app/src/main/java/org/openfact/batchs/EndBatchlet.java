package org.openfact.batchs;

import javax.batch.api.AbstractBatchlet;
import javax.batch.runtime.BatchStatus;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

@Named
@Dependent
public class EndBatchlet extends AbstractBatchlet {

    @Override
    public String process() {
        return BatchStatus.COMPLETED.toString();
    }

}
