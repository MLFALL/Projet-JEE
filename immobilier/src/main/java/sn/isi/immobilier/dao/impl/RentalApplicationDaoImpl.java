package sn.isi.immobilier.dao.impl;

import jakarta.persistence.EntityManager;
import sn.isi.immobilier.dao.RentalApplicationDao;
import sn.isi.immobilier.model.RentalApplication;
import sn.isi.immobilier.util.JPAUtil;

import java.util.List;
import java.util.Optional;

public class RentalApplicationDaoImpl implements RentalApplicationDao {
    @Override
    public RentalApplication save(RentalApplication app) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (app.getId() == null) em.persist(app);
            else app = em.merge(app);
            em.getTransaction().commit();
            return app;
        } finally { em.close(); }
    }

    @Override
    public Optional<RentalApplication> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(RentalApplication.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<RentalApplication> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT r FROM RentalApplication r", RentalApplication.class).getResultList();
        } finally { em.close(); }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            RentalApplication r = em.find(RentalApplication.class, id);
            if (r != null) em.remove(r);
            em.getTransaction().commit();
        } finally { em.close(); }
    }

    @Override
    public List<RentalApplication> findByUnit(Long unitId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT r FROM RentalApplication r WHERE r.unit.id = :uid", RentalApplication.class)
                    .setParameter("uid", unitId)
                    .getResultList();
        } finally { em.close(); }
    }

    @Override
    public List<RentalApplication> findByApplicant(Long applicantId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT r FROM RentalApplication r WHERE r.applicant.id = :aid", RentalApplication.class)
                    .setParameter("aid", applicantId)
                    .getResultList();
        } finally { em.close(); }
    }

    public List<RentalApplication> findByOwner(Long ownerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT r FROM RentalApplication r " +
                                    "WHERE r.unit.building.owner.id = :oid", RentalApplication.class)
                    .setParameter("oid", ownerId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

}
