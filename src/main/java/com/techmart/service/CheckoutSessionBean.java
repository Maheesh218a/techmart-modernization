package com.techmart.service;

import com.techmart.entity.Order;
import com.techmart.entity.OrderItem;

import jakarta.ejb.EJB;
import jakarta.ejb.Remove;
import jakarta.ejb.Stateful;
import java.util.List;

/**
 * A Stateful session bean to represent a conversational checkout process.
 * This demonstrates the usage of @Stateful EJBs.
 */
@Stateful
public class CheckoutSessionBean {

    @EJB
    private OrderService orderService;

    private Long customerId;
    private List<OrderItem> items;
    private String shippingAddress;
    private String notes;

    public void initializeCheckout(Long customerId, List<OrderItem> items) {
        this.customerId = customerId;
        this.items = items;
    }

    public void setShippingDetails(String shippingAddress, String notes) {
        this.shippingAddress = shippingAddress;
        this.notes = notes;
    }

    @Remove
    public Order completeCheckout() {
        if (customerId == null || items == null || shippingAddress == null) {
            throw new IllegalStateException("Checkout process is incomplete. Missing required data.");
        }
        
        // Delegate to the stateless OrderService to actually create the order in DB
        return orderService.createOrder(customerId, items, shippingAddress, notes);
    }
    
    @Remove
    public void cancelCheckout() {
        // Clean up conversational state without saving
        this.customerId = null;
        this.items = null;
        this.shippingAddress = null;
        this.notes = null;
    }
}
