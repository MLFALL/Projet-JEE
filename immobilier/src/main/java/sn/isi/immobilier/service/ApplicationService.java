package sn.isi.immobilier.service;

import sn.isi.immobilier.model.RentalApplication;

import java.util.List;

public interface ApplicationService {
    RentalApplication submit(Long applicantId, Long unitId, String message);
    RentalApplication approve(Long applicationId, Long approverId);
    RentalApplication reject(Long applicationId, Long approverId, String reason);
    List<RentalApplication> listByUnit(Long unitId);
    List<RentalApplication> listByOwnerUnits(Long ownerId);
    List<RentalApplication> listByApplicant(Long applicantId);
    RentalApplication findById(Long id);

    List<RentalApplication> listAll();
}