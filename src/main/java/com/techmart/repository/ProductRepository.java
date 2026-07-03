package com.techmart.repository;

import com.techmart.entity.Product;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ProductRepository extends AbstractRepository<Product> {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    public ProductRepository() {
        super(Product.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Product> findByCategory(String category) {
        return em.createQuery("SELECT p FROM Product p WHERE p.category = :category", Product.class)
                 .setParameter("category", category)
                 .getResultList();
    }
    
    public List<Product> findActiveProducts() {
        return em.createQuery("SELECT p FROM Product p WHERE p.isActive = true", Product.class)
                 .getResultList();
    }
}
