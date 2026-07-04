package com.techmart.api;

import com.techmart.entity.Warehouse;
import com.techmart.service.WarehouseService;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/warehouses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WarehouseResource {

    @EJB
    private WarehouseService warehouseService;

    @GET
    public List<Warehouse> getAllWarehouses() {
        return warehouseService.getAllWarehouses();
    }

    @GET
    @Path("/{id}")
    public Response getWarehouse(@PathParam("id") Long id) {
        Warehouse warehouse = warehouseService.getWarehouseById(id);
        if (warehouse != null) {
            return Response.ok(warehouse).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    public static class WarehouseRequest {
        public String name;
        public String location;
        public Integer capacity;
        public Boolean active;
    }

    @POST
    public Response createWarehouse(WarehouseRequest request) {
        try {
            Warehouse warehouse = new Warehouse();
            warehouse.setName(request.name);
            warehouse.setLocation(request.location);
            if (request.capacity != null) {
                warehouse.setCapacity(request.capacity);
            }
            if (request.active != null) {
                warehouse.setActive(request.active);
            }
            warehouseService.createWarehouse(warehouse);
            return Response.status(Response.Status.CREATED).entity(warehouse).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateWarehouse(@PathParam("id") Long id, WarehouseRequest request) {
        try {
            Warehouse updatedData = new Warehouse();
            updatedData.setName(request.name);
            updatedData.setLocation(request.location);
            updatedData.setCapacity(request.capacity);
            
            Warehouse warehouse = warehouseService.updateWarehouse(id, updatedData);
            return Response.ok(warehouse).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/status")
    public Response updateWarehouseStatus(@PathParam("id") Long id, @QueryParam("active") boolean active) {
        try {
            warehouseService.updateWarehouseStatus(id, active);
            return Response.ok().entity("{\"status\":\"success\"}").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
