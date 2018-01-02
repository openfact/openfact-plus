package org.clarksnut.batchs;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Startup
@Singleton
public class BatchScheduler {

    public static final long INITIAL_DELAY = 10;
    public final long PERIOD = 30;

    @Resource
    private ManagedScheduledExecutorService scheduler;

    @PostConstruct
    private void init() {
        scheduler.scheduleAtFixedRate(this::invokeBatchs, INITIAL_DELAY, PERIOD, TimeUnit.SECONDS);
    }

    private void invokeBatchs() {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        Properties properties = new Properties();
        long execID = jobOperator.start("refreshLinkedBrokers", properties);
    }

}
