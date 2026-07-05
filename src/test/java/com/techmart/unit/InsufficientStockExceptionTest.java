package com.techmart.unit;

import com.techmart.entity.Product;
import com.techmart.repository.ProductRepository;
import com.techmart.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InsufficientStockExceptionTest {

    @InjectMocks
    private InventoryService inventoryService;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testReduceStockThrowsExceptionWhenInsufficient() {
        // Arrange
        Long productId = 1L;
        Product mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setName("Gaming Laptop");
        mockProduct.setStockQuantity(5); // Only 5 in stock

        when(productRepository.find(productId)).thenReturn(mockProduct);

        // Act & Assert
        // Try to reduce 10 items when only 5 are available
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            inventoryService.reduceStock(productId, 10, "Order Checkout");
        });

        // Verify the exact exception message
        assertEquals("Insufficient stock for product Gaming Laptop", exception.getMessage());
        
        // Ensure no database edit was made
        verify(productRepository, never()).edit(any(Product.class));
    }
}
