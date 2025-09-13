package sn.isi.immobilier.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import sn.isi.immobilier.dao.RentalApplicationDao;
import sn.isi.immobilier.dao.impl.RentalApplicationDaoImpl;
import sn.isi.immobilier.model.Enums.ApplicationStatus;
import sn.isi.immobilier.model.Enums.LeaseStatus;
import sn.isi.immobilier.model.Enums.UnitStatus;
import sn.isi.immobilier.model.Lease;
import sn.isi.immobilier.model.RentalApplication;
import sn.isi.immobilier.model.Unit;
import sn.isi.immobilier.model.User;
import sn.isi.immobilier.service.ApplicationService;
import sn.isi.immobilier.util.JPAUtil;
import sn.isi.immobilier.util.LeaseUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

public class ApplicationServiceImpl implements ApplicationService {

    private final RentalApplicationDao rentalApplicationDao = new RentalApplicationDaoImpl();

    // Méthode utilitaire pour gérer EntityManager + transaction
    private <R> R executeTransaction(Function<EntityManager, R> action) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            R result = action.apply(em);
            em.getTransaction().commit();
            return result;
        } catch (RuntimeException ex) {
            em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public RentalApplication submit(Long applicantId, Long unitId, String message) {
        return executeTransaction(em -> {
            User applicant = em.find(User.class, applicantId);
            Unit unit = em.find(Unit.class, unitId);
            if (applicant == null || unit == null)
                throw new RuntimeException("Utilisateur ou unité introuvable");

            RentalApplication app = new RentalApplication();
            app.setApplicant(applicant);
            app.setUnit(unit);
            app.setMessage(message);
            app.setStatus(ApplicationStatus.PENDING);
            app.setCreatedAt(Instant.now());

            em.persist(app);
            return app;
        });
    }

    @Override
    public RentalApplication approve(Long applicationId, Long approverId) {
        return executeTransaction(em -> {
            RentalApplication app = em.find(RentalApplication.class, applicationId);
            validatePendingApplication(app);

            Unit unit = em.find(Unit.class, app.getUnit().getId());
            if (unit.getStatus() != UnitStatus.AVAILABLE)
                throw new RuntimeException("L’unité est déjà occupée");

            Lease lease = new Lease();
            lease.setUnit(unit);
            lease.setTenant(app.getApplicant());
            lease.setOwner(unit.getBuilding().getOwner());
            lease.setStartDate(LocalDate.now());
            lease.setRentAmount(unit.getRentAmount());
            lease.setStatus(LeaseStatus.ACTIVE);

            em.persist(lease);

            unit.setStatus(UnitStatus.OCCUPIED);
            em.merge(unit);

            app.setStatus(ApplicationStatus.APPROVED);
            app.setProcessedAt(Instant.now());

            em.merge(app);
            app.setTransientLease(lease);


            return app;
        });
    }

    @Override
    public RentalApplication reject(Long applicationId, Long approverId, String reason) {
        return executeTransaction(em -> {
            RentalApplication app = em.find(RentalApplication.class, applicationId);
            validatePendingApplication(app);

            app.setStatus(ApplicationStatus.REJECTED);
            app.setProcessedAt(Instant.now());
            em.merge(app);
            return app;
        });
    }
    private void validatePendingApplication(RentalApplication app) {
        if (app == null) throw new RuntimeException("Demande non trouvée");
        if (app.getStatus() != ApplicationStatus.PENDING)
            throw new RuntimeException("Demande déjà traitée");
    }

    private Lease createLease(RentalApplication app, Unit unit) {
        Lease lease = new Lease();
        lease.setUnit(unit);
        lease.setTenant(app.getApplicant());
        lease.setOwner(unit.getBuilding().getOwner());
        lease.setStartDate(LocalDate.now());
        lease.setRentAmount(unit.getRentAmount());
        lease.setStatus(LeaseStatus.ACTIVE);
        return lease;
    }

    @Override
    public List<RentalApplication> listByUnit(Long unitId) {
        return rentalApplicationDao.findByUnit(unitId);
    }

    @Override
    public List<RentalApplication> listByApplicant(Long applicantId) {
        return rentalApplicationDao.findByApplicant(applicantId);
    }

    @Override
    public RentalApplication findById(Long id) {
        return rentalApplicationDao.findById(id).orElse(null);
    }
    // Liste des demandes pour toutes les unités d'un propriétaire
    public List<RentalApplication> listByOwnerUnits(Long ownerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT a FROM RentalApplication a WHERE a.unit.building.owner.id = :ownerId";
            TypedQuery<RentalApplication> query = em.createQuery(jpql, RentalApplication.class);
            query.setParameter("ownerId", ownerId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Liste de toutes les demandes (pour ADMIN)
    public List<RentalApplication> listAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT a FROM RentalApplication a";
            TypedQuery<RentalApplication> query = em.createQuery(jpql, RentalApplication.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

}
