package com.techmart.service;

import com.techmart.entity.Notification;
import com.techmart.entity.Product;
import com.techmart.repository.NotificationRepository;
import com.techmart.repository.ProductRepository;
import jakarta.ejb.EJB;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class LowStockAlertTimer {

    @EJB
    private ProductRepository productRepository;

    @EJB
    private NotificationRepository notificationRepository;

    // Runs every minute for demonstration (in production this might be daily or hourly)
    @Schedule(hour = "*", minute = "*/1", persistent = false)
    public void checkLowStock() {
        try {
            List<Product> products = productRepository.findAll();
            List<Product> lowStockProducts = products.stream()
                .filter(p -> p.getStockQuantity() < 10 && p.getActive())
                .collect(Collectors.toList());
                
            for (Product p : lowStockProducts) {
                // Check if we already alerted recently? (Skipping complex logic for now, just generate alert)
                Notification alert = new Notification();
                alert.setCustomer(null); // System alert
                alert.setMessage("Low Stock Alert: Product ID " + p.getId() + " (" + p.getName() + ") has low stock (" + p.getStockQuantity() + " remaining).");
                alert.setType(Notification.NotificationType.LOW_STOCK);
                alert.setRead(false);
                notificationRepository.create(alert);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
