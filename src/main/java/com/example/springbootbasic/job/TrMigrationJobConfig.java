package com.example.springbootbasic.job;

import com.example.springbootbasic.job.domain.account.Accounts;
import com.example.springbootbasic.job.domain.account.AccountsRepository;
import com.example.springbootbasic.job.domain.order.Orders;
import com.example.springbootbasic.job.domain.order.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
주문 -> 정산 테이블 데이터 이관

--spring.batch.job.names=trMigrationJob
 */
@Configuration
@RequiredArgsConstructor
public class TrMigrationJobConfig {

    private JobBuilderFactory jbf;
    private StepBuilderFactory sbf;
    private OrdersRepository orderRepo;
    private AccountsRepository accountRepo;

    @Autowired
    public TrMigrationJobConfig(JobBuilderFactory jbf, StepBuilderFactory sbf, OrdersRepository orderRepo, AccountsRepository accountRepo) {
        this.jbf = jbf;
        this.sbf = sbf;
        this.orderRepo = orderRepo;
        this.accountRepo = accountRepo;
    }

    @Bean
    public Job trMigrationJob(Step trMigrationStep) {
        return jbf.get("trMigrationJob")
                .incrementer(new RunIdIncrementer())
                .start(trMigrationStep)
                .build();
    }

    @JobScope
    @Bean
    public Step trMigrationStep(ItemReader trOrdersReader, ItemProcessor trOrderToAccountsProcessor, ItemWriter trAccountsWriter, ItemWriter trAccountsWriter2) {
        return sbf.get("trMigrationStep")
                .<Orders, Accounts>chunk(5) // 5개씩 Orders 를 읽어 Accounts로 쓰겠다. chunk가 트랜잭션 단위임
                .reader(trOrdersReader)
//                .writer(new ItemWriter() {
//                    @Override
//                    public void write(List items) throws Exception {
//                        items.forEach(System.out::println);
//                    }
//                })
                .processor(trOrderToAccountsProcessor)
//                .writer(trAccountsWriter)
                .writer(trAccountsWriter2)
                .build();
    }

    /*
    1건씩 발생하므로 실제 사용할때는 bulk 옵션을 찾아봐야겠다.
     */
    @StepScope
    @Bean
    public RepositoryItemWriter<Accounts> trAccountsWriter() {
        return new RepositoryItemWriterBuilder<Accounts>()
                .repository(accountRepo)
                .methodName("save")
                .build();
    }

    /*
    writer 직접 구현
    processor 가 넘겨준걸 그대로 가져오므로
    bulk를 직접 처리하던가 최적화된 native sql을 작성한다던가 커스텀할 때 좋을듯
     */
    @StepScope
    @Bean
    public ItemWriter<Accounts> trAccountsWriter2() {
        return new ItemWriter<Accounts>() {
            @Override
            public void write(List<? extends Accounts> items) throws Exception {
                items.forEach(item -> accountRepo.save(item));
            }
        };
    }

    @StepScope
    @Bean
    public ItemProcessor<Orders, Accounts> trOrderToAccountsProcessor() {
        return new ItemProcessor<Orders, Accounts>() {
            @Override
            public Accounts process(Orders item) throws Exception {
                return Accounts.of(item);
            }
        };
    }

    @StepScope
    @Bean
    public RepositoryItemReader<Orders> trOrdersReader() {
        return new RepositoryItemReaderBuilder<Orders>()
                .name("trOrdersReader")
                .repository(orderRepo)
                .methodName("findAll")  // 예제라 이렇게 했지만 실제로는 index 잡힌 필드로 in query 하는게 좋겠다
                .pageSize(5)    // 청크 단위에 맞춰주자
                .arguments(Arrays.asList())
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }
}
