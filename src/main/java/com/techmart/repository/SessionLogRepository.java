package com.techmart.repository;

import com.techmart.entity.SessionLog;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;

@Stateless
public class SessionLogRepository extends AbstractRepository<SessionLog> {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    public SessionLogRepository() {
        super(SessionLog.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SessionLog findBySessionId(String sessionId) {
        try {
            return em.createQuery("SELECT s FROM SessionLog s WHERE s.sessionId = :sessionId", SessionLog.class)
                     .setParameter("sessionId", sessionId)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
