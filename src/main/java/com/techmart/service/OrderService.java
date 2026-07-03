package com.techmart.service;

import com.techmart.entity.Customer;
import com.techmart.entity.Order;
import com.techmart.entity.OrderItem;
import com.techmart.repository.OrderRepository;

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

    public Order createOrder(Long customerId, List<OrderItem> items, String shippingAddress, String notes) {
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

        return order;
    }

    public Order getOrderById(Long id) {
        return orderRepository.find(id);
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
    
    public void updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        Order order = orderRepository.find(orderId);
        if (order != null) {
            order.setStatus(newStatus);
            orderRepository.edit(order);
        }
    }
}
