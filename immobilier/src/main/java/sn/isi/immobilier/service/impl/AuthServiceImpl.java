package sn.isi.immobilier.service.impl;

import sn.isi.immobilier.dao.UserDao;
import sn.isi.immobilier.dao.impl.UserDaoImpl;
import sn.isi.immobilier.model.Enums.UserRole;
import sn.isi.immobilier.model.User;

import org.mindrot.jbcrypt.BCrypt;
import sn.isi.immobilier.service.AuthService;

public class AuthServiceImpl implements AuthService {
    private final UserDao userDao;

    public AuthServiceImpl() {
        this.userDao = new UserDaoImpl();
        createAdminIfNotExists(); // ← On appelle ici aussi pour init immédiat
    }

    public AuthServiceImpl(UserDao userDao) {
        this.userDao = userDao;
        createAdminIfNotExists();
    }

    private void createAdminIfNotExists() {
        String adminEmail = "admin@immobilier.com";
        String defaultPassword = "admin123";
        String fullName = "Admin Général";

        if (userDao.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setFullName(fullName);
            admin.setPhone("777777777");
            admin.setRole(UserRole.ADMIN); // Assure-toi que ADMIN est bien dans l'enum
            admin.setPasswordHash(BCrypt.hashpw(defaultPassword, BCrypt.gensalt()));
            admin.setActive(true);

            userDao.save(admin);
            System.out.println("✔️ Admin initial créé : " + adminEmail);
        }
    }
    @Override
    public User register(String email, String fullName, String phone, String rawPassword, UserRole role) {
        userDao.findByEmail(email).ifPresent(u -> { throw new RuntimeException("Email déjà utilisé"); });
        User u = new User();
        u.setEmail(email);
        u.setFullName(fullName);
        u.setPhone(phone);
        u.setRole(role);
        u.setPasswordHash(BCrypt.hashpw(rawPassword, BCrypt.gensalt(12)));
        return userDao.save(u);
    }

    @Override
    public User login(String email, String rawPassword) {
        User u = userDao.findByEmail(email).orElseThrow(() -> new RuntimeException("Identifiants invalides"));
        if (!u.isActive() || !BCrypt.checkpw(rawPassword, u.getPasswordHash())) {
            throw new RuntimeException("Identifiants invalides");
        }
        u.setLastLoginAt(java.time.Instant.now());
        userDao.save(u);
        return u;
    }
}