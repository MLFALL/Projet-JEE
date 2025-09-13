package sn.isi.immobilier.dao.impl;

import jakarta.persistence.EntityManager;
import sn.isi.immobilier.dao.LeaseDao;
import sn.isi.immobilier.model.Enums.LeaseStatus;
import sn.isi.immobilier.model.Lease;
import sn.isi.immobilier.util.JPAUtil;

import java.util.List;
import java.util.Optional;

public class LeaseDaoImpl implements LeaseDao {
    @Override
    public Lease save(Lease lease) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (lease.getId() == null) em.persist(lease);
            else lease = em.merge(lease);
            em.getTransaction().commit();
            return lease;
        } finally { em.close(); }
    }

    @Override
    public Optional<Lease> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try { return Optional.ofNullable(em.find(Lease.class, id)); }
        finally { em.close(); }
    }

    @Override
    public List<Lease> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT l FROM Lease l", Lease.class).getResultList();
        } finally { em.close(); }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Lease lease = em.find(Lease.class, id);
            if (lease != null) em.remove(lease);
            em.getTransaction().commit();
        } finally { em.close(); }
    }

    @Override
    public Optional<Lease> findActiveByUnit(Long unitId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            var q = em.createQuery("""
                SELECT l FROM Lease l 
                WHERE l.unit.id = :uid 
                  AND l.status IN (:s1, :s2)
            """, Lease.class)
                    .setParameter("uid", unitId)
                    .setParameter("s1", LeaseStatus.PENDING)
                    .setParameter("s2", LeaseStatus.ACTIVE)
                    .setMaxResults(1);
            var list = q.getResultList();
            return list.stream().findFirst();
        } finally { em.close(); }
    }

    @Override
    public List<Lease> findByTenant(Long tenantId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT l FROM Lease l WHERE l.tenant.id = :tid", Lease.class)
                    .setParameter("tid", tenantId).getResultList();
        } finally { em.close(); }
    }
    @Override
    public List<Lease> findByOwner(Long ownerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT l FROM Lease l WHERE l.owner.id = :ownerId", Lease.class)
                    .setParameter("ownerId", ownerId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
