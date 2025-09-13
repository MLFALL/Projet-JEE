package sn.isi.immobilier.service.impl;

import jakarta.persistence.EntityManager;
import sn.isi.immobilier.model.Enums.PaymentMethod;
import sn.isi.immobilier.model.Enums.PaymentStatus;
import sn.isi.immobilier.model.Enums.UserRole;
import sn.isi.immobilier.model.Lease;
import sn.isi.immobilier.model.Payment;
import sn.isi.immobilier.service.PaymentService;
import sn.isi.immobilier.util.JPAUtil;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class PaymentServiceImpl implements PaymentService {
    @Override
    public Payment markPaid(Long paymentId, BigDecimal amount, PaymentMethod method, String reference) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Payment p = em.find(Payment.class, paymentId);
            if (p == null) throw new RuntimeException("Payment not found");

            p.setAmountPaid(p.getAmountPaid().add(amount));
            p.setMethod(method);
            p.setReference(reference);
            p.setPaidAt(Instant.now());

            if (p.getAmountPaid().compareTo(p.getAmountDue()) >= 0) {
                p.setStatus(PaymentStatus.PAID);

                // Fin du bail
                Lease lease = p.getLease();
                lease.setEndDate(LocalDate.now()); // plus simple et sûr
                em.merge(lease);
            } else {
                p.setStatus(PaymentStatus.PARTIAL);
            }
            em.merge(p);

            em.getTransaction().commit();
            return p;
        }catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
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
    /**
     * Retourne les paiements dus selon le rôle de l'utilisateur.
     * @param userId l'ID de l'utilisateur
     * @param role le rôle ("TENANT" ou "OWNER")
     * @return liste des paiements
     */
    public List<Payment> findDueByUser(Long userId, String role) {
        if ("OWNER".equals(role)) {
            return findByOwner(userId);
        } else {
            return findByTenant(userId);
        }
    }


    @Override
    public Payment findById(Long paymentId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Payment.class, paymentId);
        } finally {
            em.close();
        }
    }
    // Dans PaymentServiceImpl
    public boolean isPaidForLease(Long leaseId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(p) FROM Payment p WHERE p.lease.id = :lid AND p.status <> :paid", Long.class)
                    .setParameter("lid", leaseId)
                    .setParameter("paid", PaymentStatus.PAID)
                    .getSingleResult();
            return count == 0;
        } finally {
            em.close();
        }
    }

    // Paiements dus par locataire
    public List<Payment> findByTenant(Long tenantId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Payment p WHERE p.lease.tenant.id = :uid " +
                                    "AND (p.status = :pending OR p.status = :partial) " +
                                    "ORDER BY p.lease.startDate DESC",
                            Payment.class)
                    .setParameter("uid", tenantId)
                    .setParameter("pending", PaymentStatus.PENDING)
                    .setParameter("partial", PaymentStatus.PARTIAL)
                    .getResultList();
        } finally {
            em.close();
        }
    }
    // Paiements liés à un propriétaire
    public List<Payment> findByOwner(Long ownerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Payment p WHERE p.lease.owner.id = :uid " +
                                    "ORDER BY p.lease.startDate DESC",
                            Payment.class)
                    .setParameter("uid", ownerId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
    @Override
    public Payment createPayment(Payment payment) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(payment);
            em.getTransaction().commit();
            return payment;
        } finally {
            em.close();
        }
    }



}
