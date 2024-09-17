package org.code.payment.domain;

import org.code.payment.infrastructure.messaging.PaymentMessageProducer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jms.core.JmsTemplate;

import java.math.BigDecimal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PaymentServiceTest {

    @Test
    void processPayment_shouldSendToQueue() {
        JmsTemplate jmsTemplate = Mockito.mock(JmsTemplate.class);
        PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);
        PaymentService paymentService = new PaymentService(paymentRepository);
        PaymentMessageProducer pms = new PaymentMessageProducer(jmsTemplate);

        Payment payment = new Payment("1", new BigDecimal("100.00"), "USD", "User123");
        paymentService.processPayment(payment);
        pms.sendPayment(payment);

        verify(paymentRepository, times(1)).save(payment);
        verify(jmsTemplate).convertAndSend("payment.queue", payment);
    }

}