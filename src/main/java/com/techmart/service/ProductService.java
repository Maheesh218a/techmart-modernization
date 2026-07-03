package com.techmart.service;

import com.techmart.entity.Product;
import com.techmart.repository.ProductRepository;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.util.List;

@Stateless
public class ProductService {

    @EJB
    private ProductRepository productRepository;

    public void addProduct(Product product) {
        productRepository.create(product);
    }

    public Product getProductById(Long id) {
        return productRepository.find(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public List<Product> getActiveProducts() {
        return productRepository.findActiveProducts();
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public void updateProduct(Product product) {
        productRepository.edit(product);
    }
    
    public void deactivateProduct(Long id) {
        Product product = productRepository.find(id);
        if (product != null) {
            product.setActive(false);
            productRepository.edit(product);
        }
    }
}
