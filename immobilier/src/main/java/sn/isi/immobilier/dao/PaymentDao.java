package sn.isi.immobilier.dao;

import sn.isi.immobilier.model.Payment;

import java.util.List;

public interface PaymentDao extends CrudDao<Payment, Long> {
    List<Payment> findDueByLease(Long leaseId);
    List<Payment> findLatePayments();
}
