package com.techmart.api;

import com.techmart.entity.InventoryLog;
import com.techmart.repository.InventoryLogRepository;
import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/inventory")
@Produces(MediaType.APPLICATION_JSON)
public class InventoryResource {

    @EJB
    private InventoryLogRepository inventoryLogRepository;

    @GET
    @Path("/logs")
    public Response getInventoryLogs() {
        try {
            List<InventoryLog> logs = inventoryLogRepository.findAll();
            
            // Map to DTO to avoid JSON serialization issues with Lazy loaded fields
            List<java.util.Map<String, Object>> result = logs.stream().map(log -> {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("id", log.getId());
                map.put("createdAt", log.getCreatedAt());
                map.put("changeType", log.getChangeType());
                map.put("quantityChange", log.getQuantityChange());
                map.put("previousStock", log.getPreviousStock());
                map.put("newStock", log.getNewStock());
                map.put("reason", log.getReason());
                
                if (log.getProduct() != null) {
                    java.util.Map<String, Object> prod = new java.util.HashMap<>();
                    prod.put("id", log.getProduct().getId());
                    prod.put("name", log.getProduct().getName());
                    map.put("product", prod);
                }
                
                if (log.getWarehouse() != null) {
                    java.util.Map<String, Object> wh = new java.util.HashMap<>();
                    wh.put("id", log.getWarehouse().getId());
                    wh.put("name", log.getWarehouse().getName());
                    map.put("warehouse", wh);
                }
                
                return map;
            }).collect(java.util.stream.Collectors.toList());
            
            return Response.ok(result).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
