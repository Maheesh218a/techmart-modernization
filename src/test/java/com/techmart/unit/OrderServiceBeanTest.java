package com.techmart.unit;

import com.techmart.entity.Customer;
import com.techmart.entity.Order;
import com.techmart.entity.OrderItem;
import com.techmart.entity.Product;
import com.techmart.repository.NotificationRepository;
import com.techmart.repository.OrderRepository;
import com.techmart.service.AsyncNotificationService;
import com.techmart.service.CartService;
import com.techmart.service.CustomerService;
import com.techmart.service.InventoryService;
import com.techmart.service.OrderService;
import com.techmart.service.PerformanceMetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceBeanTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryService inventoryService;
    
    @Mock
    private CustomerService customerService;
    
    @Mock
    private CartService cartService;

    @Mock
    private PerformanceMetricsService metricsService;

    @Mock
    private AsyncNotificationService asyncNotificationService;
    
    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private OrderService orderService;

    private Customer testCustomer;
    private List<OrderItem> testItems;

    @BeforeEach
    public void setup() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setEmail("test@example.com");
        testCustomer.setLoyaltyPoints(0);

        testItems = new ArrayList<>();
        
        Product product = new Product();
        product.setId(10L);
        product.setName("Test Laptop");
        
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("1000.00"));
        
        testItems.add(item);
    }

    @Test
    public void testCreateOrder_Success() {
        // Arrange
        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);
        when(inventoryService.checkStock(eq(10L), eq(2))).thenReturn(true);
        doNothing().when(orderRepository).create(any(Order.class));
        doNothing().when(inventoryService).reduceStock(anyLong(), anyInt(), anyString());
        doNothing().when(cartService).clearCart(1L);

        // Act
        Order createdOrder = orderService.createOrder(1L, testItems, "123 Test St", "Leave at door");

        // Assert
        assertNotNull(createdOrder);
        assertEquals(testCustomer, createdOrder.getCustomer());
        assertEquals("123 Test St", createdOrder.getShippingAddress());
        assertEquals(new BigDecimal("2000.00"), createdOrder.getTotalAmount());
        assertEquals(Order.OrderStatus.PENDING, createdOrder.getStatus());
        
        // Loyalty points: 2000 / 100 = 20 points
        assertEquals(20, testCustomer.getLoyaltyPoints());

        // Verify interactions
        verify(orderRepository, times(1)).create(any(Order.class));
        verify(inventoryService, times(1)).reduceStock(eq(10L), eq(2), anyString());
        verify(cartService, times(1)).clearCart(1L);
        verify(metricsService, times(1)).recordOrderProcessing(anyLong());
        verify(notificationRepository, times(1)).create(any());
        verify(asyncNotificationService, times(1)).sendEmailNotification(eq("test@example.com"), anyString());
    }

    @Test
    public void testCreateOrder_CustomerNotFound() {
        // Arrange
        when(customerService.getCustomerById(99L)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(99L, testItems, "123 Test St", "Leave at door");
        });

        assertEquals("Customer not found", exception.getMessage());
        
        // Ensure nothing was saved
        verify(orderRepository, never()).create(any(Order.class));
    }
}
