package org.code.payment.domain;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment processPayment(Payment payment) {
        // Business logic such as validating payment, processing payment, etc.
        paymentRepository.save(payment);
        return payment;
    }
}
