package sn.isi.immobilier.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import sn.isi.immobilier.model.Enums.PaymentMethod;
import sn.isi.immobilier.model.Enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="payment")
public class Payment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false) @JoinColumn(name="lease_id")
    private Lease lease;

    @Column(name = "due_date", nullable=false)
    private LocalDate dueDate;

    @Column(name="amount_due", nullable=false, precision=12, scale=2)
    private java.math.BigDecimal amountDue;

    @Column(name="amount_paid", precision=12, scale=2)
    private java.math.BigDecimal amountPaid = java.math.BigDecimal.ZERO;

    @Column(name = "paid_at")
    private java.time.Instant paidAt;

    @Enumerated(EnumType.STRING)
    @Column(length=20) private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(length=100) private String reference;

    public Payment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Lease getLease() { return lease; }
    public void setLease(Lease lease) { this.lease = lease; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public BigDecimal getAmountDue() { return amountDue; }
    public void setAmountDue(BigDecimal amountDue) { this.amountDue = amountDue; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public Instant getPaidAt() { return paidAt; }
    public void setPaidAt(Instant paidAt) { this.paidAt = paidAt; }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
}
