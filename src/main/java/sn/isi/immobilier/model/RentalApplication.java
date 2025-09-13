package sn.isi.immobilier.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import sn.isi.immobilier.model.Enums.ApplicationStatus;

import java.time.Instant;

@Entity
@Table(name="rental_application")
public class RentalApplication {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false) @JoinColumn(name="unit_id")
    private Unit unit;

    @ManyToOne(optional=false) @JoinColumn(name="applicant_id")
    private User applicant;

    @Column(columnDefinition="TEXT") private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "created_at",nullable=false)
    private java.time.Instant createdAt = java.time.Instant.now();
    @Column(name = "processed_at")
    private java.time.Instant processedAt;

    @Transient
    private Lease transientLease;

    public Lease getTransientLease() { return transientLease; }
    public void setTransientLease(Lease lease) { this.transientLease = lease; }


    public RentalApplication() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Unit getUnit() { return unit; }
    public void setUnit(Unit unit) { this.unit = unit; }

    public User getApplicant() { return applicant; }
    public void setApplicant(User applicant) { this.applicant = applicant; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getProcessedAt() { return processedAt; }
    public void setProcessedAt(Instant processedAt) { this.processedAt = processedAt; }

}
