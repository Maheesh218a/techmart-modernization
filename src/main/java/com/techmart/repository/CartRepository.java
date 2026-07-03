package com.techmart.repository;

import com.techmart.entity.Cart;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;

@Stateless
public class CartRepository extends AbstractRepository<Cart> {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    public CartRepository() {
        super(Cart.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Cart findByCustomerId(Long customerId) {
        try {
            return em.createQuery("SELECT DISTINCT c FROM Cart c LEFT JOIN FETCH c.items i LEFT JOIN FETCH i.product WHERE c.customer.id = :customerId", Cart.class)
                     .setParameter("customerId", customerId)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
