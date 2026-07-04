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
            return Response.ok(logs).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
