package com.techmart.service;

import com.techmart.entity.Customer;
import com.techmart.entity.Order;
import com.techmart.entity.OrderItem;
import com.techmart.entity.Notification;
import com.techmart.repository.OrderRepository;
import com.techmart.repository.NotificationRepository;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class OrderService {

    @EJB
    private OrderRepository orderRepository;

    @EJB
    private InventoryService inventoryService;
    
    @EJB
    private CustomerService customerService;
    
    @EJB
    private CartService cartService;

    @EJB
    private PerformanceMetricsService metricsService;

    @EJB
    private AsyncNotificationService asyncNotificationService;
    
    @EJB
    private NotificationRepository notificationRepository;

    public Order createOrder(Long customerId, List<OrderItem> items, String shippingAddress, String notes) {
        long startTime = System.currentTimeMillis();
        
        Customer customer = customerService.getCustomerById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setShippingAddress(shippingAddress);
        order.setNotes(notes);
        
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : items) {
            // Check stock
            if (!inventoryService.checkStock(item.getProduct().getId(), item.getQuantity())) {
                throw new IllegalStateException("Insufficient stock for product " + item.getProduct().getName());
            }
            
            // Calculate subtotal
            BigDecimal subtotal = item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()));
            item.setSubtotal(subtotal);
            total = total.add(subtotal);
            
            order.addItem(item);
        }

        order.setTotalAmount(total);
        orderRepository.create(order);
        
        // After creating the order, reduce the stock
        for (OrderItem item : items) {
            inventoryService.reduceStock(item.getProduct().getId(), item.getQuantity(), "Order Placed - ID: " + order.getId());
        }
        
        // Clear the user's cart in the DB
        cartService.clearCart(customerId);

        // Record metrics
        long processingTime = System.currentTimeMillis() - startTime;
        metricsService.recordOrderProcessing(processingTime);
        
        // Save notification to DB
        try {
            Notification notif = new Notification();
            notif.setCustomer(customer);
            notif.setOrder(order);
            notif.setType(Notification.NotificationType.ORDER_CONFIRMED);
            notif.setMessage("Your order #" + order.getId() + " has been placed successfully.");
            notificationRepository.create(notif);
        } catch (Exception e) {
            System.err.println("Failed to save notification to DB: " + e.getMessage());
        }

        // Send async notification (simulated email)
        asyncNotificationService.sendEmailNotification(
            customer.getEmail(), 
            "Your order #" + order.getId() + " has been placed successfully."
        );

        return order;
    }

    public Order getOrderById(Long id) {
        return orderRepository.find(id);
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public void updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        Order order = orderRepository.find(orderId);
        if (order != null) {
            order.setStatus(newStatus);
            orderRepository.edit(order);
        }
    }
}
