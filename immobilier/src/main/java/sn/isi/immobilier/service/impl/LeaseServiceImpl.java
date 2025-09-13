package sn.isi.immobilier.service.impl;

import jakarta.persistence.EntityManager;
import sn.isi.immobilier.dao.LeaseDao;
import sn.isi.immobilier.dao.impl.LeaseDaoImpl;
import sn.isi.immobilier.model.Enums.LeaseStatus;
import sn.isi.immobilier.model.Enums.PaymentStatus;
import sn.isi.immobilier.model.Enums.UnitStatus;
import sn.isi.immobilier.model.Lease;
import sn.isi.immobilier.model.Unit;
import sn.isi.immobilier.service.LeaseService;
import sn.isi.immobilier.service.PaymentService;
import sn.isi.immobilier.util.JPAUtil;

import java.util.List;

public class LeaseServiceImpl implements LeaseService {
    private final LeaseDao leaseDao = new LeaseDaoImpl();
    private final PaymentService paymentService = new PaymentServiceImpl();


    @Override
    public Lease findById(Long id) {
        Lease lease = leaseDao.findById(id).orElse(null);
        if (lease != null) {
            updatePaymentStatus(lease);
        }
        return lease;    }

    @Override
    public List<Lease> findByTenant(Long tenantId) {
        List<Lease> leases = leaseDao.findByTenant(tenantId);
        leases.forEach(this::updatePaymentStatus);
        return leases;
    }

    @Override
    public List<Lease> findByOwner(Long ownerId) {
        List<Lease> leases = leaseDao.findByOwner(ownerId);
        leases.forEach(this::updatePaymentStatus);
        return leases;
    }

    @Override
    public Lease create(Lease lease) {
        return leaseDao.save(lease);
    }

    @Override
    public Lease update(Long id, Lease updatedLease) {
        Lease existing = leaseDao.findById(id).orElseThrow(() -> new RuntimeException("Bail non trouvé"));
        existing.setStartDate(updatedLease.getStartDate());
        existing.setEndDate(updatedLease.getEndDate());
        existing.setRentAmount(updatedLease.getRentAmount());
        existing.setDepositAmount(updatedLease.getDepositAmount());
        existing.setStatus(updatedLease.getStatus());
        return leaseDao.save(existing);
    }

    @Override
    public void terminate(Long leaseId) {
        Lease lease = findById(leaseId);
        if (lease == null) throw new RuntimeException("Bail introuvable");

        lease.setStatus(sn.isi.immobilier.model.Enums.LeaseStatus.TERMINATED);
        Unit unit = lease.getUnit();
        if (unit != null) {
            unit.setStatus(sn.isi.immobilier.model.Enums.UnitStatus.AVAILABLE);
        }
        leaseDao.save(lease);
    }

    @Override
    public void delete(Long id) {
        leaseDao.deleteById(id);
    }

    @Override
    public List<Lease> findAll() {
        List<Lease> leases = leaseDao.findAll();
        leases.forEach(this::updatePaymentStatus);
        return leases;
    }
    private void updatePaymentStatus(Lease lease) {
        boolean paid = paymentService.findDueByLease(lease.getId())
                .stream()
                .allMatch(p -> p.getStatus() == PaymentStatus.PAID);
        lease.setPaymentPaid(paid); // Assure-toi que Lease a un champ boolean paymentPaid + getter/setter
    }
}
