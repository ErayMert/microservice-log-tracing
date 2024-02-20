package com.demo.customer.scheduler;

import com.demo.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
@ConditionalOnProperty(value = "scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class OrderHistoryScheduler {

    private final CustomerService customerService;

    @Scheduled(cron = "*/30 * * * * *")
    public void getJobOrderCountHistory() {
        log.info("Job order count history started");
        Long totalCount = customerService.orderJobInfo();
        log.info("Total order count : {}", totalCount);
    }

}