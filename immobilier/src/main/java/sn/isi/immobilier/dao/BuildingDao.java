package sn.isi.immobilier.dao;

import sn.isi.immobilier.model.Building;

import java.util.List;

public interface BuildingDao extends CrudDao<Building, Long> {
    List<Building> findByOwner(Long ownerId);
    List<Building> searchByCity(String city);
    List<Building> findAvailableByOwner(Long ownerId);

}
