package org.code.payment.application;

import lombok.AllArgsConstructor;
import org.code.payment.domain.Payment;
import org.code.payment.domain.PaymentService;
import org.code.payment.infrastructure.messaging.PaymentMessageProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMessageProducer paymentMessageProducer;

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        // Send payment details to ActiveMQ queue
        paymentMessageProducer.sendPayment(payment);
        return ResponseEntity.ok(payment);
    }
}
