package com.techmart.messaging;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:global/jms/TechMartNotificationQueue"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class NotificationMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(NotificationMDB.class.getName());

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String text = ((TextMessage) message).getText();
                // Simulate processing the notification (e.g. sending an email or SMS)
                LOGGER.log(Level.INFO, ">>> ASYNC NOTIFICATION RECEIVED: Processing message... {0}", text);
                
                // Here we could parse the message and save it to the MessageLog or Notification entity
                // For demonstration, we simply log it to show asynchronous behavior.
                
            } else {
                LOGGER.log(Level.WARNING, "Received non-text message");
            }
        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "Error processing message", e);
        }
    }
}
