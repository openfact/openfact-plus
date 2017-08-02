package org.openfact.batchs;

import org.jberet.rest.client.BatchClient;
import org.jberet.rest.entity.JobExecutionEntity;
import org.jberet.schedule.JobSchedule;
import org.jberet.schedule.JobScheduleConfig;
import org.jberet.schedule.JobScheduleConfigBuilder;

import javax.ejb.Local;
import javax.ejb.Stateless;

@Stateless
@Local(ManagedScheduledBatch.class)
public class ManagedScheduledBatchBean implements ManagedScheduledBatch {

    public void main(String[] args) throws Exception {
        BatchClient batchClient = new BatchClient("");

        final JobExecutionEntity jobExecutionEntity = batchClient.startJob(BatchContants.READ_MAIL_JOB_NAME, null);

        final JobScheduleConfig scheduleConfig = JobScheduleConfigBuilder.newInstance()
                .jobName(BatchContants.READ_MAIL_JOB_NAME)
                .jobParameters(null)
                .initialDelay(1)
                .interval(1)
                .build();

        JobSchedule jobSchedule = batchClient.schedule(scheduleConfig);
    }

    @Override
    public void runJob() {

    }
}
