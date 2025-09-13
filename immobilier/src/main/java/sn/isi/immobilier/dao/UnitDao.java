package sn.isi.immobilier.dao;

import sn.isi.immobilier.model.Unit;

import java.math.BigDecimal;
import java.util.List;

public interface UnitDao extends CrudDao<Unit, Long> {
    List<Unit> searchAvailable(String city, Integer minRooms, BigDecimal maxRent);
    List<Unit> findByBuilding(Long buildingId);
}