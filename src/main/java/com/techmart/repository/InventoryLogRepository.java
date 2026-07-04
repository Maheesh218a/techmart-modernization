package com.techmart.repository;

import com.techmart.entity.InventoryLog;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class InventoryLogRepository {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    public void create(InventoryLog log) {
        em.persist(log);
    }

    public List<InventoryLog> findAll() {
        return em.createQuery("SELECT l FROM InventoryLog l LEFT JOIN FETCH l.product LEFT JOIN FETCH l.warehouse ORDER BY l.createdAt DESC", InventoryLog.class)
                 .setMaxResults(100)
                 .getResultList();
    }
}
