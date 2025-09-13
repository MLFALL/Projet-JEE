package sn.isi.immobilier.dao.impl;

import jakarta.persistence.EntityManager;
import sn.isi.immobilier.dao.BuildingDao;
import sn.isi.immobilier.model.Building;
import sn.isi.immobilier.util.JPAUtil;

import java.util.List;
import java.util.Optional;

public class BuildingDaoImpl implements BuildingDao {
    @Override
    public Building save(Building b) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (b.getId() == null) em.persist(b);
            else b = em.merge(b);
            em.getTransaction().commit();
            return b;
        } finally { em.close(); }
    }

    @Override
    public Optional<Building> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try { return Optional.ofNullable(em.find(Building.class, id)); }
        finally { em.close(); }
    }

    @Override
    public List<Building> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try { return em.createQuery("SELECT b FROM Building b", Building.class).getResultList(); }
        finally { em.close(); }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Building b = em.find(Building.class, id);
            if (b != null) em.remove(b);
            em.getTransaction().commit();
        } finally { em.close(); }
    }

    @Override
    public List<Building> findByOwner(Long ownerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT b FROM Building b WHERE b.owner.id = :oid", Building.class)
                    .setParameter("oid", ownerId).getResultList();
        } finally { em.close(); }
    }

    @Override
    public List<Building> searchByCity(String city) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT b FROM Building b WHERE LOWER(b.city) = LOWER(:city)", Building.class)
                    .setParameter("city", city).getResultList();
        } finally { em.close(); }
    }

    @Override
    public List<Building> findAvailableByOwner(Long ownerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT b FROM Building b " +
                                    "WHERE b.owner.id = :ownerId " +
                                    "AND NOT EXISTS (" +
                                    "   SELECT l FROM Lease l " +
                                    "   WHERE l.unit.building = b AND l.status = sn.isi.immobilier.model.Enums.LeaseStatus.ACTIVE" +
                                    ")",
                            Building.class
                    )
                    .setParameter("ownerId", ownerId)
                    .getResultList();
        } finally {
            em.close();
        }
    }


}
