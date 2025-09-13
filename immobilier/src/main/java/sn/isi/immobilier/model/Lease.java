package sn.isi.immobilier.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import sn.isi.immobilier.model.Enums.LeaseStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="lease")
public class Lease {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false) @JoinColumn(name="unit_id")
    private Unit unit;

    @ManyToOne(optional=false) @JoinColumn(name="tenant_id")
    private User tenant;

    @ManyToOne(optional=false) @JoinColumn(name="owner_id")
    private User owner;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date",nullable = false)
    private LocalDate endDate;


    @Column(name="rent_amount", nullable=false, precision=12, scale=2)
    private java.math.BigDecimal rentAmount;
    // dans Lease.java
    @Transient
    private boolean paymentPaid;

    public boolean isPaymentPaid() { return paymentPaid; }
    public void setPaymentPaid(boolean paymentPaid) { this.paymentPaid = paymentPaid; }


    @Column(name="deposit_amount", precision=12, scale=2)
    private java.math.BigDecimal depositAmount = java.math.BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private LeaseStatus status = LeaseStatus.PENDING;

    @Column(name = "created_at",nullable=false)
    private Instant createdAt = java.time.Instant.now();

    public Lease() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Unit getUnit() { return unit; }
    public void setUnit(Unit unit) { this.unit = unit; }

    public User getTenant() { return tenant; }
    public void setTenant(User tenant) { this.tenant = tenant; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getRentAmount() { return rentAmount; }
    public void setRentAmount(BigDecimal rentAmount) { this.rentAmount = rentAmount; }

    public BigDecimal getDepositAmount() { return depositAmount; }
    public void setDepositAmount(BigDecimal depositAmount) { this.depositAmount = depositAmount; }

    public LeaseStatus getStatus() { return status; }
    public void setStatus(LeaseStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
