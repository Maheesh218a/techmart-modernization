package com.techmart.repository;

import com.techmart.entity.Customer;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;

@Stateless
public class CustomerRepository extends AbstractRepository<Customer> {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    public CustomerRepository() {
        super(Customer.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Customer findByEmail(String email) {
        try {
            return em.createQuery("SELECT c FROM Customer c WHERE c.email = :email", Customer.class)
                     .setParameter("email", email)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
