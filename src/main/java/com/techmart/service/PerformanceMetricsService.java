package com.techmart.service;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Singleton
@Startup
public class PerformanceMetricsService {

    private LocalDateTime systemStartTime;
    private AtomicInteger activeUsers;
    private AtomicInteger totalOrdersProcessed;
    private AtomicLong totalProcessingTimeMs;

    @PostConstruct
    public void init() {
        systemStartTime = LocalDateTime.now();
        activeUsers = new AtomicInteger(0);
        totalOrdersProcessed = new AtomicInteger(0);
        totalProcessingTimeMs = new AtomicLong(0);
    }

    public void incrementActiveUsers() {
        activeUsers.incrementAndGet();
    }

    public void decrementActiveUsers() {
        activeUsers.decrementAndGet();
    }

    public void recordOrderProcessing(long processingTimeMs) {
        totalOrdersProcessed.incrementAndGet();
        totalProcessingTimeMs.addAndGet(processingTimeMs);
    }

    public LocalDateTime getSystemStartTime() {
        return systemStartTime;
    }

    public int getActiveUsers() {
        return activeUsers.get();
    }

    public int getTotalOrdersProcessed() {
        return totalOrdersProcessed.get();
    }

    public double getAverageOrderProcessingTimeMs() {
        int count = totalOrdersProcessed.get();
        if (count == 0) return 0;
        return (double) totalProcessingTimeMs.get() / count;
    }
}

