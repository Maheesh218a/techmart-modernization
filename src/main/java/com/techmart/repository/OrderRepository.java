package com.techmart.repository;

import com.techmart.entity.Order;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class OrderRepository extends AbstractRepository<Order> {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    public OrderRepository() {
        super(Order.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Order> findByCustomerId(Long customerId) {
        return em.createQuery("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product WHERE o.customer.id = :customerId ORDER BY o.orderDate DESC", Order.class)
                 .setParameter("customerId", customerId)
                 .getResultList();
    }
    
    public List<Order> findByStatus(Order.OrderStatus status) {
        return em.createQuery("SELECT o FROM Order o WHERE o.status = :status", Order.class)
                 .setParameter("status", status)
                 .getResultList();
    }

    @Override
    public List<Order> findAll() {
        return em.createQuery("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product ORDER BY o.orderDate DESC", Order.class).getResultList();
    }
}
