package com.techmart.api;

import com.techmart.entity.Order;
import com.techmart.entity.SessionLog;
import com.techmart.repository.OrderRepository;
import com.techmart.repository.SessionLogRepository;
import com.techmart.service.PerformanceMetricsService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

    @EJB
    private PerformanceMetricsService metricsService;
    
    @EJB
    private OrderRepository orderRepository;
    
    @EJB
    private SessionLogRepository sessionLogRepository;

    @GET
    @Path("/metrics")
    public Response getSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Custom app metrics
        long activeSessions = sessionLogRepository.findAll().stream().filter(s -> Boolean.TRUE.equals(s.getActive())).count();
        metrics.put("activeUsers", activeSessions);
        metrics.put("totalOrdersProcessed", orderRepository.countTotalOrders());
        metrics.put("pendingOrders", orderRepository.countOrdersByStatus(Order.OrderStatus.PENDING));
        metrics.put("shippedOrders", orderRepository.countOrdersByStatus(Order.OrderStatus.SHIPPED));
        metrics.put("deliveredOrders", orderRepository.countOrdersByStatus(Order.OrderStatus.DELIVERED));
        metrics.put("cancelledOrders", orderRepository.countOrdersByStatus(Order.OrderStatus.CANCELLED));
        
        metrics.put("avgOrderProcessingTime", metricsService.getAverageOrderProcessingTimeMs());
        
        // JVM memory metrics
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        metrics.put("memoryTotal", totalMemory);
        metrics.put("memoryUsed", usedMemory);
        metrics.put("memoryFree", freeMemory);
        
        // Simulating EJB pool size and load metrics since direct Payara JMX isn't used
        // In a real environment, we would use JMX or MicroProfile Metrics
        int activeThreads = Thread.activeCount();
        metrics.put("activeThreads", activeThreads);
        
        return Response.ok(metrics).build();
    }
}
