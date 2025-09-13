package sn.isi.immobilier.service;

import sn.isi.immobilier.model.Payment;
import sn.isi.immobilier.model.Enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {
    Payment markPaid(Long paymentId, BigDecimal amount, PaymentMethod method, String reference);
    List<Payment> findDueByLease(Long leaseId);
    List<Payment> findLatePayments();
    List<Payment> findDueByUser(Long userId,String role);
    Payment findById(Long paymentId);

    boolean isPaidForLease(Long id);

    Payment createPayment(Payment payment);
}
