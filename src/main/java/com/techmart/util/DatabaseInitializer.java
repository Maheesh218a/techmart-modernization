package com.techmart.util;

import com.techmart.entity.Product;
import com.techmart.entity.Warehouse;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;

@Singleton
@Startup
public class DatabaseInitializer {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    @PostConstruct
    public void init() {
        try {
            // Check if products already exist
            List<Product> existingProducts = em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
            
            if (existingProducts.isEmpty()) {
                System.out.println("No products found in DB. Initializing with sample data...");

                // Create a default Warehouse
                Warehouse warehouse = new Warehouse();
                warehouse.setName("Main TechMart Warehouse");
                warehouse.setLocation("Colombo, Sri Lanka");
                warehouse.setCapacity(5000);
                em.persist(warehouse);

                // Add sample products with generated images
                addProduct("Dell XPS 13", "High-performance laptop with InfinityEdge display.", new BigDecimal("350000.00"), "Laptops", "images/laptop1.png", warehouse);
                addProduct("MacBook Pro M2", "Apple's latest pro laptop with M2 chip.", new BigDecimal("480000.00"), "Laptops", "images/laptop2.png", warehouse);
                addProduct("Logitech MX Master 3", "Advanced wireless mouse for productivity.", new BigDecimal("35000.00"), "Accessories", "images/mouse1.png", warehouse);
                addProduct("Sony WH-1000XM5", "Industry-leading noise canceling headphones.", new BigDecimal("120000.00"), "Audio", "images/audio1.png", warehouse);
                addProduct("Samsung 34\" Curved Monitor", "Ultra-wide curved monitor for immersive viewing.", new BigDecimal("185000.00"), "Monitors", "images/monitor1.png", warehouse);
                addProduct("Keychron K2 Keyboard", "Mechanical wireless keyboard.", new BigDecimal("42000.00"), "Accessories", "images/keyboard1.png", warehouse);
                
                System.out.println("Sample data initialization complete.");
            }
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    private void addProduct(String name, String description, BigDecimal price, String category, String imageUrl, Warehouse warehouse) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setCategory(category);
        p.setImageUrl(imageUrl);
        p.setStockQuantity(50);
        p.setWarehouse(warehouse);
        em.persist(p);
    }
}
