    package sn.isi.immobilier.model;

    import jakarta.persistence.*;
    import jakarta.validation.constraints.Email;
    import jakarta.validation.constraints.NotBlank;
    import java.time.Instant;
    import sn.isi.immobilier.model.Enums.UserRole;

    @Entity @Table(name="app_user")
    public class User {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Email @NotBlank
        @Column(nullable=false, unique=true, length=255)
        private String email;

        @Column(name="password_hash", nullable=false, length=60)
        private String passwordHash;

        @NotBlank @Column(name="full_name", nullable=false, length=150)
        private String fullName;

        @Column(length=30) private String phone;

        @Enumerated(EnumType.STRING)
        @Column(nullable=false, length=20)
        private UserRole role;

        @Column(nullable=false) private boolean active = true;

        @Column(name="created_at", nullable=false)
        private Instant createdAt = Instant.now();

        @Column(name="last_login_at") private Instant lastLoginAt;

        // Constructors
        public User() {}

        // Getters / Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPasswordHash() { return passwordHash; }
        public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public UserRole getRole() { return role; }
        public void setRole(UserRole role) { this.role = role; }

        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }

        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

        public Instant getLastLoginAt() { return lastLoginAt; }
        public void setLastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    }