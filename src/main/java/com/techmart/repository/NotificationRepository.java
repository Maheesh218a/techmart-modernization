package com.techmart.repository;

import com.techmart.entity.Notification;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class NotificationRepository extends AbstractRepository<Notification> {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    public NotificationRepository() {
        super(Notification.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
