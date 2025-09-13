package sn.isi.immobilier.dao;

import sn.isi.immobilier.model.User;

import java.util.Optional;

public interface UserDao extends CrudDao<User, Long> {
    Optional<User> findByEmail(String email);
}