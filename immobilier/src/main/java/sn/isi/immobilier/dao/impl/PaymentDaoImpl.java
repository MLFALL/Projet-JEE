package sn.isi.immobilier.dao.impl;

import jakarta.persistence.EntityManager;
import sn.isi.immobilier.dao.PaymentDao;
import sn.isi.immobilier.model.Enums.PaymentStatus;
import sn.isi.immobilier.model.Payment;
import sn.isi.immobilier.util.JPAUtil;

import java.util.List;
import java.util.Optional;

public class PaymentDaoImpl implements PaymentDao {
    @Override
    public Payment save(Payment p) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (p.getId() == null) em.persist(p);
            else p = em.merge(p);
            em.getTransaction().commit();
            return p;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Payment> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Payment.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Payment> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Payment p", Payment.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Payment p = em.find(Payment.class, id);
            if (p != null) em.remove(p);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Payment> findDueByLease(Long leaseId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Payment p WHERE p.lease.id = :lid AND p.status = :st", Payment.class)
                    .setParameter("lid", leaseId)
                    .setParameter("st", PaymentStatus.PENDING)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Payment> findLatePayments() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Payment p WHERE p.status = :late", Payment.class)
                    .setParameter("late", PaymentStatus.LATE)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
