package sn.isi.immobilier.service;

import sn.isi.immobilier.model.Building;

import java.util.List;

public interface BuildingService {
    Building create(Building b);
    Building update(Building b);
    Building createOrUpdate(Building b); // ajout cohérent avec l'utilisation dans la servlet
    List<Building> findAll();
    List<Building> findByOwner(Long ownerId);
    List<Building> searchByCity(String city);
    List<Building> findAvailableByOwner(Long ownerId);

    Building findById(Long id);
    void delete(Long id);
}