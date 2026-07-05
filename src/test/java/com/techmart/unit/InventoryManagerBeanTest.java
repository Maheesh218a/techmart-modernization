package com.techmart.unit;

import com.techmart.entity.Product;
import com.techmart.entity.Warehouse;
import com.techmart.entity.InventoryLog;
import com.techmart.repository.ProductRepository;
import com.techmart.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InventoryManagerBeanTest {

    @InjectMocks
    private InventoryService inventoryService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EntityManager em;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCheckStock_Sufficient() {
        Long productId = 1L;
        Product p = new Product();
        p.setStockQuantity(50);
        
        when(productRepository.find(productId)).thenReturn(p);

        boolean result = inventoryService.checkStock(productId, 20);
        assertTrue(result, "Stock should be sufficient");
    }

    @Test
    public void testCheckStock_Insufficient() {
        Long productId = 1L;
        Product p = new Product();
        p.setStockQuantity(10);
        
        when(productRepository.find(productId)).thenReturn(p);

        boolean result = inventoryService.checkStock(productId, 20);
        assertFalse(result, "Stock should be insufficient");
    }

    @Test
    public void testReduceStock_Success() {
        Long productId = 1L;
        Product p = new Product();
        p.setId(productId);
        p.setStockQuantity(50);
        
        Warehouse w = new Warehouse();
        w.setId(10L);
        p.setWarehouse(w);

        when(productRepository.find(productId)).thenReturn(p);

        inventoryService.reduceStock(productId, 10, "Order Placed");

        assertEquals(40, p.getStockQuantity());
        verify(productRepository, times(1)).edit(p);
        verify(em, times(1)).persist(any(InventoryLog.class));
    }
}
