package org.code.payment.infrastructure.messaging;

import lombok.AllArgsConstructor;
import org.code.payment.domain.Payment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PaymentMessageProducer {

    private final JmsTemplate jmsTemplate;
    private final String paymentQueue = "payment.queue";

    public void sendPayment(Payment payment) {
        jmsTemplate.convertAndSend(paymentQueue, payment);
    }
}
