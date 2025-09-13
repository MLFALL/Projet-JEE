package sn.isi.immobilier.service;

import sn.isi.immobilier.model.Enums.UnitStatus;
import sn.isi.immobilier.model.Unit;

import java.math.BigDecimal;
import java.util.List;

public interface UnitService {
    List<Unit> listAvailable(String city, Integer minRooms, BigDecimal maxRent);
    Unit create(Long buildingId, String unitNumber, int rooms, BigDecimal rent);
    Unit update(Long id, Unit updatedUnit);
    Unit updateStatus(Long unitId, UnitStatus status);
    Unit findById(Long id);
    void delete(Long id);
}
