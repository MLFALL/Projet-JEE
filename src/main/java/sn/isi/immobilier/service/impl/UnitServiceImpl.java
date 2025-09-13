package sn.isi.immobilier.service.impl;

import jakarta.persistence.EntityManager;
import sn.isi.immobilier.dao.UnitDao;
import sn.isi.immobilier.dao.impl.UnitDaoImpl;
import sn.isi.immobilier.model.Building;
import sn.isi.immobilier.model.Enums.UnitStatus;
import sn.isi.immobilier.model.Unit;
import sn.isi.immobilier.service.UnitService;
import sn.isi.immobilier.util.JPAUtil;

import java.math.BigDecimal;
import java.util.List;

public class UnitServiceImpl implements UnitService {
    private final UnitDao unitDao = new UnitDaoImpl();

    @Override
    public List<Unit> listAvailable(String city, Integer minRooms, BigDecimal maxRent) {
        return unitDao.searchAvailable(city, minRooms, maxRent);
    }

    @Override
    public Unit create(Long buildingId, String unitNumber, int rooms, BigDecimal rent) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Building building = em.find(Building.class, buildingId);
            if (building == null) {
                throw new IllegalArgumentException("Bâtiment introuvable avec ID : " + buildingId);
            }
            Unit u = new Unit();
            u.setBuilding(building);
            u.setUnitNumber(unitNumber);
            u.setRooms(rooms);
            u.setRentAmount(rent);
            u.setStatus(UnitStatus.AVAILABLE);
            return unitDao.save(u);
        } finally {
            em.close();
        }
    }

    @Override
    public Unit update(Long id, Unit updatedUnit) {
        Unit existing = unitDao.findById(id).orElseThrow(() -> new RuntimeException("Unité non trouvée"));
        existing.setUnitNumber(updatedUnit.getUnitNumber());
        existing.setRooms(updatedUnit.getRooms());
        existing.setRentAmount(updatedUnit.getRentAmount());
        existing.setAreaM2(updatedUnit.getAreaM2());
        existing.setFloor(updatedUnit.getFloor());
        existing.setFeatures(updatedUnit.getFeatures());
        existing.setStatus(updatedUnit.getStatus());
        return unitDao.save(existing);
    }

    @Override
    public Unit updateStatus(Long unitId, UnitStatus status) {
        Unit u = unitDao.findById(unitId).orElseThrow(() -> new RuntimeException("Unité non trouvée"));
        u.setStatus(status);
        return unitDao.save(u);
    }

    @Override
    public Unit findById(Long id) {
        return unitDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        unitDao.deleteById(id);
    }
}
