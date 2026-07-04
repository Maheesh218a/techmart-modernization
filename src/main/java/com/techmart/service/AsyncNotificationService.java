package com.techmart.service;

import jakarta.ejb.AsyncResult;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@Stateless
public class AsyncNotificationService {

    private static final Logger logger = Logger.getLogger(AsyncNotificationService.class.getName());

    @Asynchronous
    public Future<Boolean> sendEmailNotification(String emailAddress, String message) {
        logger.info("Starting asynchronous email sending to " + emailAddress);
        try {
            // Simulate network delay for email sending
            Thread.sleep(3000);
            logger.info("Successfully sent email to " + emailAddress + ": " + message);
            return new AsyncResult<>(true);
        } catch (InterruptedException e) {
            logger.severe("Asynchronous email sending interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
            return new AsyncResult<>(false);
        }
    }
}
