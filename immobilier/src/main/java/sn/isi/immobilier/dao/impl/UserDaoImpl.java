package sn.isi.immobilier.dao.impl;

import sn.isi.immobilier.dao.UserDao;
import sn.isi.immobilier.model.User;
import sn.isi.immobilier.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List; import java.util.Optional;

public class UserDaoImpl implements UserDao {
    @Override
    public User save(User u) {
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
    public Optional<User> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try { return Optional.ofNullable(em.find(User.class, id)); }
        finally { em.close(); }
    }

    @Override
    public List<User> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try { return em.createQuery("SELECT u FROM User u", User.class).getResultList(); }
        finally { em.close(); }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            User u = em.find(User.class, id);
            if (u != null) em.remove(u);
            em.getTransaction().commit();
        } finally { em.close(); }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            var q = em.createQuery("SELECT u FROM User u WHERE u.email = :e", User.class)
                    .setParameter("e", email)
                    .setMaxResults(1);
            List<User> l = q.getResultList();
            return l.stream().findFirst();
        } finally { em.close(); }
    }
}