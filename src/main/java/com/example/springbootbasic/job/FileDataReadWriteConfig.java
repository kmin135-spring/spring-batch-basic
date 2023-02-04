package com.example.springbootbasic.job;

import com.example.springbootbasic.job.domain.Player;
import com.example.springbootbasic.job.domain.PlayerYear;
import com.example.springbootbasic.job.mapper.PlayerFieldSetMapper;
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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

/*
--spring.batch.job.names=fileDataReadWriteJob
 */
@Configuration
@RequiredArgsConstructor
public class FileDataReadWriteConfig {

    private JobBuilderFactory jbf;
    private StepBuilderFactory sbf;

    @Autowired
    public FileDataReadWriteConfig(JobBuilderFactory jbf, StepBuilderFactory sbf) {
        this.jbf = jbf;
        this.sbf = sbf;
    }

    @Bean
    public Job fileDataReadWriteJob(Step fileDataReadWriteStep) {
        return jbf.get("fileDataReadWriteJob")
                .incrementer(new RunIdIncrementer())
                .start(fileDataReadWriteStep)
                .build();
    }

    @JobScope
    @Bean
    public Step fileDataReadWriteStep(ItemReader playerItemReader, ItemProcessor playerItemProcessor, ItemWriter playerYearItemWriter) {
        return sbf.get("fileDataReadWriteStep")
                .<Player, PlayerYear>chunk(5)
                .reader(playerItemReader)
//                .writer(new ItemWriter() {
//                    @Override
//                    public void write(List items) throws Exception {
//                        items.forEach(System.out::println);
//                    }
//                })
                .processor(playerItemProcessor)
                .writer(playerYearItemWriter)
                .build();
    }

    @StepScope
    @Bean
    public FlatFileItemWriter<PlayerYear> playerYearItemWriter() {
        BeanWrapperFieldExtractor<PlayerYear> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"ID", "lastName", "position", "yearsExperience"});
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator<PlayerYear> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        FileSystemResource outputResource = new FileSystemResource("player-year.txt");

        return new FlatFileItemWriterBuilder<PlayerYear>()
                .name("playerYearItemWriter")
                .resource(outputResource)
                .lineAggregator(lineAggregator)
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<Player, PlayerYear> playerItemProcessor() {
        return new ItemProcessor<Player, PlayerYear>() {
            @Override
            public PlayerYear process(Player item) throws Exception {
                return PlayerYear.of(item);
            }
        };
    }

    @StepScope
    @Bean
    public FlatFileItemReader<Player> playerItemReader() {
        return new FlatFileItemReaderBuilder<Player>()
                .name("playerItemReader")
                .resource(new FileSystemResource("players.csv"))
                .lineTokenizer(new DelimitedLineTokenizer())
                .fieldSetMapper(new PlayerFieldSetMapper())
                .linesToSkip(1)
                .build();
    }
}
