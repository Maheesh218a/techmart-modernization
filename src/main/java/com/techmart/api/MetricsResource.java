package com.techmart.api;

import com.techmart.service.PerformanceMetricsService;
import com.techmart.repository.OrderRepository;
import com.techmart.repository.CustomerRepository;
import com.techmart.entity.Order;

import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/metrics")
@Produces(MediaType.APPLICATION_JSON)
public class MetricsResource {

    @EJB
    private PerformanceMetricsService metricsService;

    @EJB
    private OrderRepository orderRepository;

    @EJB
    private CustomerRepository customerRepository;

    @GET
    public Response getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("systemStartTime", metricsService.getSystemStartTime().toString());
        
        // Active registered users in database
        metrics.put("activeUsers", customerRepository.countActiveCustomers());
        
        // Use DB counts for total and specific statuses
        metrics.put("totalOrdersProcessed", orderRepository.countTotalOrders());
        metrics.put("pendingOrders", orderRepository.countOrdersByStatus(Order.OrderStatus.PENDING));
        metrics.put("shippedOrders", orderRepository.countOrdersByStatus(Order.OrderStatus.SHIPPED));
        metrics.put("deliveredOrders", orderRepository.countOrdersByStatus(Order.OrderStatus.DELIVERED));
        metrics.put("cancelledOrders", orderRepository.countOrdersByStatus(Order.OrderStatus.CANCELLED));
        metrics.put("averageOrderProcessingTimeMs", metricsService.getAverageOrderProcessingTimeMs());
        
        // Add memory stats
        Runtime runtime = Runtime.getRuntime();
        metrics.put("totalMemory", runtime.totalMemory());
        metrics.put("freeMemory", runtime.freeMemory());
        metrics.put("maxMemory", runtime.maxMemory());
        
        return Response.ok(metrics).build();
    }
}
