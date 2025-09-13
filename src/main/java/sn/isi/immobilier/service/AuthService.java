package sn.isi.immobilier.service;

import sn.isi.immobilier.model.Enums.UserRole;
import sn.isi.immobilier.model.User;

public interface AuthService {
    User register(String email, String fullName, String phone, String rawPassword, UserRole role);
    User login(String email, String rawPassword); // renvoie l'utilisateur si OK, sinon exception

}
