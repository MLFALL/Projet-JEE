package sn.isi.immobilier.service;

import sn.isi.immobilier.model.Lease;

import java.util.List;

public interface LeaseService {
    Lease findById(Long id);
    List<Lease> findByTenant(Long tenantId);
    Lease create(Lease lease);
    Lease update(Long id, Lease lease);
    void terminate(Long leaseId);
    void delete(Long id);
    List<Lease> findAll();

    List<Lease> findByOwner(Long userId);
}