package sn.isi.immobilier.service;

import sn.isi.immobilier.model.User;

import java.util.List;

public interface UserService {
    /*User register(String fullName, String email, String password, UserRole role);
    User authenticate(String email, String password);*/
    void update(User user);
    void delete(Long id);
    User findById(Long id);
    List<User> findAll();
}