package com.techmart.api;

import com.techmart.entity.Order;
import com.techmart.entity.OrderItem;
import com.techmart.messaging.NotificationProducer;
import com.techmart.service.OrderService;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @EJB
    private OrderService orderService;

    @EJB
    private NotificationProducer notificationProducer;

    // A simple DTO to receive an order request
    public static class OrderRequest {
        public Long customerId;
        public String shippingAddress;
        public String notes;
        public List<OrderItem> items;
    }

    @POST
    public Response createOrder(OrderRequest request) {
        try {
            Order order = orderService.createOrder(
                request.customerId, 
                request.items, 
                request.shippingAddress, 
                request.notes
            );
            
            // Send asynchronous notification
            notificationProducer.sendNotification("Order placed successfully. Order ID: " + order.getId());
            
            return Response.status(Response.Status.CREATED).entity(order).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GET
    @Path("/{id}")
    public Response getOrder(@PathParam("id") Long id) {
        Order order = orderService.getOrderById(id);
        if (order != null) {
            return Response.ok(order).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/{id}/status")
    public Response updateOrderStatus(@PathParam("id") Long id, @QueryParam("status") String status) {
        try {
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status);
            orderService.updateOrderStatus(id, newStatus);
            return Response.ok().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid status").build();
        }
    }
}
