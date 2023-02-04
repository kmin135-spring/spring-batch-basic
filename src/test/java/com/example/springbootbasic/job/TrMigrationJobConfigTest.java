package com.example.springbootbasic.job;

import com.example.springbootbasic.SpringBatchTestConfig;
import com.example.springbootbasic.job.domain.account.AccountsRepository;
import com.example.springbootbasic.job.domain.order.Orders;
import com.example.springbootbasic.job.domain.order.OrdersRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBatchTest
@SpringBootTest(classes = {SpringBatchTestConfig.class, TrMigrationJobConfig.class})
class TrMigrationJobConfigTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private OrdersRepository orderRepo;
    @Autowired
    private AccountsRepository accountRepo;

    @AfterEach
    public void cleanUpEach() {
        orderRepo.deleteAll();
        accountRepo.deleteAll();
    }

    @Test
    public void successWithNoData() throws Exception {
        JobExecution execution = jobLauncherTestUtils.launchJob();

        Assertions.assertEquals(ExitStatus.COMPLETED, execution.getExitStatus());
        Assertions.assertEquals(0, accountRepo.count());
    }

    @Test
    public void successExistData() throws Exception {
        Orders o1 = new Orders(null, "kakao gift 111 ", 15000, new Date());
        Orders o2 = new Orders(null, "kakao gift 222", 16400, new Date());

        orderRepo.save(o1);
        orderRepo.save(o2);

        JobExecution execution = jobLauncherTestUtils.launchJob();

        Assertions.assertEquals(ExitStatus.COMPLETED, execution.getExitStatus());
        Assertions.assertEquals(2, accountRepo.count());
    }
}