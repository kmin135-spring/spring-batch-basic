package com.example.springbootbasic.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class SampleScheduler {
    private final Job helloWorldJob;
    private final JobLauncher jobLauncher;

    public SampleScheduler(Job helloWorldJob, JobLauncher jobLauncher) {
        this.helloWorldJob = helloWorldJob;
        this.jobLauncher = jobLauncher;
    }

    @Scheduled(cron = "0 */1 * * * *") // 1분마다
    public void helloWorldJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters param = new JobParameters(Collections.singletonMap("requestTime", new JobParameter(System.currentTimeMillis())));
        jobLauncher.run(helloWorldJob, param);
    }
}
