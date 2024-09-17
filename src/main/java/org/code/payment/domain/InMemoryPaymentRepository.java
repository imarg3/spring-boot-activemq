package org.code.payment.domain;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryPaymentRepository implements PaymentRepository {

    private final Map<String, Payment> payments = new HashMap<>();

    @Override
    public void save(Payment payment) {
        payments.put(payment.getPaymentId(), payment);
    }

    @Override
    public Optional<Payment> findByPaymentId(String paymentId) {
        return Optional.ofNullable(payments.get(paymentId));
    }
}
