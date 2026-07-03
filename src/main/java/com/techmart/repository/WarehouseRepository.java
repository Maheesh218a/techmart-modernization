package com.techmart.repository;

import com.techmart.entity.Warehouse;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class WarehouseRepository extends AbstractRepository<Warehouse> {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    public WarehouseRepository() {
        super(Warehouse.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
