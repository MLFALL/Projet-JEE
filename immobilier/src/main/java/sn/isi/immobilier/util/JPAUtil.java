package sn.isi.immobilier.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    private static final EntityManagerFactory emf = build();

    private static EntityManagerFactory build() {
        try { return Persistence.createEntityManagerFactory("immoPU"); }
        catch (Exception ex) { throw new RuntimeException("EMF init failed", ex); }
    }
    public static EntityManager getEntityManager() { return emf.createEntityManager(); }

}
