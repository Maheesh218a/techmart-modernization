package com.techmart.integration;

import com.techmart.entity.Order;
import com.techmart.service.OrderService;
import jakarta.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ArquillianExtension.class)
public class OrderIntegrationTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "com.techmart.entity")
                .addPackages(true, "com.techmart.repository")
                .addPackages(true, "com.techmart.service")
                .addAsResource(new File("src/main/resources/META-INF/persistence.xml"), "META-INF/persistence.xml")
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"), "beans.xml");
    }

    @EJB
    private OrderService orderService;

    @Test
    public void testOrderServiceInjection() {
        // This test simply verifies that Arquillian successfully deployed the application
        // and that the EJB container can inject the OrderService bean correctly.
        assertNotNull(orderService, "OrderService should be injected by the EJB container");
        
        // Let's call a simple read-only method to verify database connectivity and EJB functionality
        List<Order> allOrders = orderService.getAllOrders();
        assertNotNull(allOrders, "Order list should not be null");
        // It might be empty if the DB is fresh, but the call should succeed without throwing exceptions
    }
}
