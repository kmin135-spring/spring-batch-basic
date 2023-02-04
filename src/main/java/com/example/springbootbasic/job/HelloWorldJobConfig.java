package com.example.springbootbasic.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
--spring.batch.job.names=helloWorldJob
 */
@Configuration
@RequiredArgsConstructor
public class HelloWorldJobConfig {

    private JobBuilderFactory jbf;
    private StepBuilderFactory sbf;

    @Autowired
    public HelloWorldJobConfig(JobBuilderFactory jbf, StepBuilderFactory sbf) {
        this.jbf = jbf;
        this.sbf = sbf;
    }

    @Bean
    public Job helloWorldJob(Step helloWorldStep) {
        return jbf.get("helloWorldJob")
                .incrementer(new RunIdIncrementer())
                .start(helloWorldStep)
                .build();
    }

    @JobScope
    @Bean
    public Step helloWorldStep(Tasklet helloWorldTasklet) {
        return sbf.get("helloWorldStep")
                .tasklet(helloWorldTasklet)
                .build();
    }

    @StepScope
    @Bean
    public Tasklet helloWorldTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("Hello world Spring Batch");
                return RepeatStatus.FINISHED;
            }
        };
    }
}
