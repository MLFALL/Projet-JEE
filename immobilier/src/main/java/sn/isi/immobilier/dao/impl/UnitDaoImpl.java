package sn.isi.immobilier.dao.impl;


import sn.isi.immobilier.dao.UnitDao;
import sn.isi.immobilier.model.Enums.UnitStatus;
import sn.isi.immobilier.model.Unit;
import sn.isi.immobilier.util.JPAUtil;

import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class UnitDaoImpl implements UnitDao {
    @Override
    public Unit save(Unit u) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (u.getId() == null) em.persist(u);
            else u = em.merge(u);
            em.getTransaction().commit();
            return u;
        } finally { em.close(); }
    }

    @Override
    public Optional<Unit> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try { return Optional.ofNullable(em.find(Unit.class, id)); }
        finally { em.close(); }
    }

    @Override
    public List<Unit> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try { return em.createQuery("SELECT u FROM Unit u", Unit.class).getResultList(); }
        finally { em.close(); }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Unit u = em.find(Unit.class, id);
            if (u != null) em.remove(u);
            em.getTransaction().commit();
        } finally { em.close(); }
    }

    @Override
    public List<Unit> searchAvailable(String city, Integer minRooms, BigDecimal maxRent) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT u FROM Unit u WHERE u.status = :status");
            if (city != null) jpql.append(" AND LOWER(u.building.city) = LOWER(:city)");
            if (minRooms != null) jpql.append(" AND u.rooms >= :rooms");
            if (maxRent != null) jpql.append(" AND u.rentAmount <= :rent");
            var q = em.createQuery(jpql.toString(), Unit.class);
            q.setParameter("status", UnitStatus.AVAILABLE);
            if (city != null) q.setParameter("city", city);
            if (minRooms != null) q.setParameter("rooms", minRooms);
            if (maxRent != null) q.setParameter("rent", maxRent);
            return q.getResultList();
        } finally { em.close(); }
    }

    @Override
    public List<Unit> findByBuilding(Long buildingId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM Unit u WHERE u.building.id = :bid", Unit.class)
                    .setParameter("bid", buildingId).getResultList();
        } finally { em.close(); }
    }
}