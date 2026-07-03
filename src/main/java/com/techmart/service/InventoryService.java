package com.techmart.service;

import com.techmart.entity.InventoryLog;
import com.techmart.entity.Product;
import com.techmart.entity.Warehouse;
import com.techmart.repository.AbstractRepository;
import com.techmart.repository.ProductRepository;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class InventoryService {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    @EJB
    private ProductRepository productRepository;

    public boolean checkStock(Long productId, int quantity) {
        Product product = productRepository.find(productId);
        return product != null && product.getStockQuantity() >= quantity;
    }

    public void reduceStock(Long productId, int quantity, String reason) {
        Product product = productRepository.find(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        if (product.getStockQuantity() < quantity) {
            throw new IllegalStateException("Insufficient stock for product " + product.getName());
        }

        int previousStock = product.getStockQuantity();
        int newStock = previousStock - quantity;
        
        product.setStockQuantity(newStock);
        productRepository.edit(product);

        // Record log
        InventoryLog log = new InventoryLog();
        log.setProduct(product);
        log.setWarehouse(product.getWarehouse());
        log.setChangeType(InventoryLog.ChangeType.STOCK_OUT);
        log.setQuantityChange(-quantity);
        log.setPreviousStock(previousStock);
        log.setNewStock(newStock);
        log.setReason(reason);
        em.persist(log);
    }
}
