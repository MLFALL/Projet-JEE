package sn.isi.immobilier.service.impl;

import sn.isi.immobilier.model.User;
import sn.isi.immobilier.service.UserService;
import sn.isi.immobilier.dao.UserDao;
import sn.isi.immobilier.dao.impl.UserDaoImpl;


import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserDao userDao = new UserDaoImpl();

   /* @Override
    public User register(String fullName, String email, String password, UserRole role) {
        if (userDao.findByEmail(email) != null) {
            throw new RuntimeException("Email déjà utilisé");
        }
        User u = new User();
        u.setFullName(fullName);
        u.setEmail(email);
        u.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        u.setRole(role);
        return userDao.save(u);
    }

    @Override
    public User authenticate(String email, String password) {
        return userDao.findByEmail(email)
                .filter(u -> BCrypt.checkpw(password, u.getPasswordHash()))
                .orElse(null); // ✅ Optional
    }*/

    @Override
    public void update(User user) {
        userDao.save(user);
    }

    @Override
    public void delete(Long id) {
        userDao.deleteById(id);
    }

    @Override
    public User findById(Long id) {
        return userDao.findById(id).orElse(null);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }
}