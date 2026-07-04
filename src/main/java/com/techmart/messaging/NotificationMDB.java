package com.techmart.messaging;

import com.techmart.entity.MessageLog;
import com.techmart.repository.MessageLogRepository;
import jakarta.ejb.EJB;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDateTime;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:global/jms/TechMartNotificationQueue"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class NotificationMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(NotificationMDB.class.getName());

    @EJB
    private MessageLogRepository messageLogRepository;

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String text = ((TextMessage) message).getText();
                // Simulate processing the notification (e.g. sending an email or SMS)
                LOGGER.log(Level.INFO, ">>> ASYNC NOTIFICATION RECEIVED: Processing message... {0}", text);
                
                try {
                    MessageLog log = new MessageLog();
                    log.setMessageType("EMAIL_NOTIFICATION");
                    log.setDestination("Customer Email");
                    log.setPayload(text);
                    log.setStatus(MessageLog.MessageStatus.PROCESSED);
                    log.setProcessedAt(LocalDateTime.now());
                    messageLogRepository.create(log);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to save message log", e);
                }
                
            } else {
                LOGGER.log(Level.WARNING, "Received non-text message");
            }
        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "Error processing message", e);
        }
    }
}
