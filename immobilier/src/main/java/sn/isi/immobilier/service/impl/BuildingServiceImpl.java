package sn.isi.immobilier.service.impl;

import sn.isi.immobilier.dao.BuildingDao;
import sn.isi.immobilier.dao.impl.BuildingDaoImpl;
import sn.isi.immobilier.model.Building;
import sn.isi.immobilier.service.BuildingService;

import java.util.List;

public class BuildingServiceImpl implements BuildingService {
    private final BuildingDao buildingDao = new BuildingDaoImpl();

    @Override
    public Building create(Building b) {
        return buildingDao.save(b);
    }

    @Override
    public Building update(Building b) {
        return buildingDao.save(b);
    }

    @Override
    public Building createOrUpdate(Building b) {
        return buildingDao.save(b);
    }

    @Override
    public List<Building> findByOwner(Long ownerId) {
        return buildingDao.findByOwner(ownerId);
    }
    @Override
    public List<Building> findAvailableByOwner(Long ownerId) {
        return buildingDao.findAvailableByOwner(ownerId);
    }


    @Override
    public List<Building> searchByCity(String city) {
        return buildingDao.searchByCity(city);
    }

    @Override
    public Building findById(Long id) {
        return buildingDao.findById(id).orElse(null);
    }

    @Override
    public List<Building> findAll() {
        return buildingDao.findAll();
    }

    @Override
    public void delete(Long id) {
        buildingDao.deleteById(id);
    }
}
