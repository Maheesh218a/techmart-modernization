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
            // Using JNDI lookup to get the Stateful session bean
            // Since this is a REST endpoint, it's inherently stateless, so we look up a new
            // instance of the CheckoutSessionBean for this specific request process.
            javax.naming.InitialContext ic = new javax.naming.InitialContext();
            com.techmart.service.CheckoutSessionBean checkoutBean = 
                (com.techmart.service.CheckoutSessionBean) ic.lookup("java:module/CheckoutSessionBean");
            
            // Step 1: Initialize checkout with customer and items
            checkoutBean.initializeCheckout(request.customerId, request.items);
            
            // Step 2: Set shipping details
            checkoutBean.setShippingDetails(request.shippingAddress, request.notes);
            
            // Step 3: Complete checkout and create order (this also removes the Stateful bean)
            Order order = checkoutBean.completeCheckout();
            
            // Send asynchronous notification (JMS)
            notificationProducer.sendNotification("Order placed successfully via Stateful Checkout. Order ID: " + order.getId());
            
            return Response.status(Response.Status.CREATED).entity(order).build();
        } catch (Exception e) {
            e.printStackTrace();
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
