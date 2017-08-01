package org.openfact.batch;

import javax.annotation.PostConstruct;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.Properties;

@Startup
@Singleton
public class BatchStarter {

    public static final String JOB_NAME = "idpJob";

    @PostConstruct
    private void init() {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        Properties properties = new Properties();
        long execID = jobOperator.start(JOB_NAME, properties);
    }

}
