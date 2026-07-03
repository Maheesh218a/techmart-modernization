package com.techmart.repository;

import com.techmart.entity.Order;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
        return em.createQuery("SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.orderDate DESC", Order.class)
                 .setParameter("customerId", customerId)
                 .getResultList();
    }
    
    public List<Order> findByStatus(Order.OrderStatus status) {
        return em.createQuery("SELECT o FROM Order o WHERE o.status = :status", Order.class)
                 .setParameter("status", status)
                 .getResultList();
    }
}
