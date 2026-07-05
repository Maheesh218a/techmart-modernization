package com.techmart.unit;

import com.techmart.entity.Cart;
import com.techmart.entity.Customer;
import com.techmart.repository.CartRepository;
import com.techmart.service.CartService;
import com.techmart.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CartServiceBeanTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CustomerService customerService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCartByCustomerId_ExistingCart() {
        // Arrange
        Long customerId = 1L;
        Cart mockCart = new Cart();
        mockCart.setId(100L);
        when(cartRepository.findByCustomerId(customerId)).thenReturn(mockCart);

        // Act
        Cart result = cartService.getCartByCustomerId(customerId);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        verify(customerService, never()).getCustomerById(anyLong());
    }

    @Test
    public void testGetCartByCustomerId_NewCartCreated() {
        // Arrange
        Long customerId = 2L;
        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);

        when(cartRepository.findByCustomerId(customerId)).thenReturn(null);
        when(customerService.getCustomerById(customerId)).thenReturn(mockCustomer);

        // Act
        Cart result = cartService.getCartByCustomerId(customerId);

        // Assert
        assertNotNull(result);
        assertEquals(mockCustomer, result.getCustomer());
        verify(cartRepository, times(1)).create(any(Cart.class));
    }

    @Test
    public void testClearCart() {
        // Arrange
        Long customerId = 3L;
        Cart mockCart = new Cart();
        // Assume some items were in the cart (abstracted in logic)
        
        when(cartRepository.findByCustomerId(customerId)).thenReturn(mockCart);

        // Act
        cartService.clearCart(customerId);

        // Assert
        assertTrue(mockCart.getItems().isEmpty());
        verify(cartRepository, times(1)).edit(mockCart);
    }
}
