package org.code.payment.domain;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository {
    void save(Payment payment);
    Optional<Payment> findByPaymentId(String paymentId);
}
