package com.techmart.api;

import com.techmart.service.PerformanceMetricsService;

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

    @GET
    public Response getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("systemStartTime", metricsService.getSystemStartTime().toString());
        metrics.put("activeUsers", metricsService.getActiveUsers());
        metrics.put("totalOrdersProcessed", metricsService.getTotalOrdersProcessed());
        metrics.put("averageOrderProcessingTimeMs", metricsService.getAverageOrderProcessingTimeMs());
        
        // Add memory stats
        Runtime runtime = Runtime.getRuntime();
        metrics.put("totalMemory", runtime.totalMemory());
        metrics.put("freeMemory", runtime.freeMemory());
        metrics.put("maxMemory", runtime.maxMemory());
        
        return Response.ok(metrics).build();
    }
}
