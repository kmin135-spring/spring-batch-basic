package com.example.springbootbasic.job;

import com.example.springbootbasic.job.validator.FileParamValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/*
파일 이름 파라미터 전달과 검증
--spring.batch.job.names=validatedParamJob -fileName=test.csv
 */
@Configuration
@RequiredArgsConstructor
public class ValidatedParamJobConfig {

    private JobBuilderFactory jbf;
    private StepBuilderFactory sbf;

    @Autowired
    public ValidatedParamJobConfig(JobBuilderFactory jbf, StepBuilderFactory sbf) {
        this.jbf = jbf;
        this.sbf = sbf;
    }

    @Bean
    public Job validatedParamJob(Step validatedParamStep) {
        return jbf.get("validatedParamJob")
                .incrementer(new RunIdIncrementer())
//                .validator(new FileParamValidator()) // 단일 validator 적용
                .validator(multiValidator())
                .start(validatedParamStep)
                .build();
    }

    /*
    여러 개의 validator를 적용
     */
    private CompositeJobParametersValidator multiValidator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(List.of(new FileParamValidator()));
        return validator;
    }

    @JobScope
    @Bean
    public Step validatedParamStep(Tasklet validatedParamTasklet) {
        return sbf.get("validatedParamStep")
                .tasklet(validatedParamTasklet)
                .build();
    }

    @StepScope
    @Bean
    public Tasklet validatedParamTasklet(@Value("#{jobParameters['fileName']}") String fileName) {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("Filename : " + fileName);
                System.out.println("validated Param Tasklet");
                return RepeatStatus.FINISHED;
            }
        };
    }
}
