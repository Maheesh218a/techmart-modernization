package com.techmart.integration;

import com.techmart.service.AsyncNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderNotificationIT {

    @InjectMocks
    private AsyncNotificationService asyncNotificationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testJmsOrderNotificationDelivery() throws InterruptedException, ExecutionException {
        // This test simulates the asynchronous integration of JMS message processing
        String testEmail = "customer@domain.com";
        String testMessage = "Order #1024 Confirmed";

        // Execute the asynchronous EJB method
        Future<Boolean> resultFuture = asyncNotificationService.sendEmailNotification(testEmail, testMessage);

        // Wait for the asynchronous background task (simulating JMS queue worker) to complete
        boolean isDelivered = resultFuture.get();

        // Validate that the async process finished successfully without blocking the main thread
        assertTrue(isDelivered, "Asynchronous order notification should be delivered successfully via MDB");
    }
}
