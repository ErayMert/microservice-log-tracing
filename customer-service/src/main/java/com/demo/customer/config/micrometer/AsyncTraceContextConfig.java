package com.demo.customer.config.micrometer;

import io.micrometer.context.ContextExecutorService;
import io.micrometer.context.ContextSnapshotFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class AsyncTraceContextConfig implements AsyncConfigurer {

    private final ThreadPoolTaskExecutor taskExecutor;

    @Override
    public Executor getAsyncExecutor() {
        return ContextExecutorService.wrap(taskExecutor.getThreadPoolExecutor(),
                ContextSnapshotFactory.builder().build()::captureAll);
    }
}
