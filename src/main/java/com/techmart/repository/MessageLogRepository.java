package com.techmart.repository;

import com.techmart.entity.MessageLog;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class MessageLogRepository extends AbstractRepository<MessageLog> {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    public MessageLogRepository() {
        super(MessageLog.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
