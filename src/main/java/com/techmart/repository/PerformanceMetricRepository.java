package com.techmart.repository;

import com.techmart.entity.PerformanceMetric;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class PerformanceMetricRepository extends AbstractRepository<PerformanceMetric> {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    public PerformanceMetricRepository() {
        super(PerformanceMetric.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
