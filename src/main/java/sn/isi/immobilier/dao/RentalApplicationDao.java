package sn.isi.immobilier.dao;

import sn.isi.immobilier.model.RentalApplication;

import java.util.List;

public interface RentalApplicationDao extends CrudDao<RentalApplication, Long> {
    List<RentalApplication> findByUnit(Long unitId);
    List<RentalApplication> findByApplicant(Long applicantId);
}