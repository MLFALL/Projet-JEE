package sn.isi.immobilier.dao;

import sn.isi.immobilier.model.Lease;

import java.util.List;
import java.util.Optional;

public interface LeaseDao extends CrudDao<Lease, Long> {
    Optional<Lease> findActiveByUnit(Long unitId);
    List<Lease> findByTenant(Long tenantId);

    List<Lease> findByOwner(Long ownerId);
}