package org.code.payment.infrastructure.messaging;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.code.payment.domain.Payment;
import org.code.payment.domain.PaymentService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@Component
@AllArgsConstructor
@Slf4j
public class PaymentMessageConsumer {

    private final PaymentService paymentService;

    @JmsListener(destination = "payment.queue")
    public void receivePayment(TextMessage message) throws JMSException {
        // Process payment once received from ActiveMQ
        log.info("Received payment message {}", message.getText());
        // paymentService.processPayment(payment);
    }
}
