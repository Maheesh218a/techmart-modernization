package com.techmart.messaging;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.Queue;

@JMSDestinationDefinition(
    name = "java:global/jms/TechMartNotificationQueue",
    interfaceName = "javax.jms.Queue",
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
