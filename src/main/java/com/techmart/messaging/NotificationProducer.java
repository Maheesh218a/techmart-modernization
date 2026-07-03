package com.techmart.messaging;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jms.JMSConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSDestinationDefinition;
import jakarta.jms.Queue;

@JMSDestinationDefinition(
    name = "java:global/jms/TechMartNotificationQueue",
    interfaceName = "jakarta.jms.Queue",
    destinationName = "TechMartNotificationQueue"
)
@Stateless
public class NotificationProducer {

    @Inject
    @JMSConnectionFactory("java:comp/DefaultJMSConnectionFactory")
    private JMSContext context;

    @Resource(lookup = "java:global/jms/TechMartNotificationQueue")
    private Queue notificationQueue;

    public void sendNotification(String message) {
        context.createProducer().send(notificationQueue, message);
    }
}
