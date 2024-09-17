package org.code.payment.application;

import org.code.payment.domain.Payment;
import org.code.payment.domain.PaymentService;
import org.code.payment.infrastructure.messaging.PaymentMessageProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentMessageProducer paymentMessageProducer;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
    }

    @Test
    @DisplayName("Create Payment - Should Return 200 OK and Payment Object")
    void createPayment() throws Exception {
        // Arrange: Prepare a sample payment object
        Payment payment = new Payment("1", new BigDecimal("100.00"), "USD", "User123");
        when(paymentService.processPayment(any(Payment.class))).thenReturn(payment);

        // Act & Assert: Make the POST request and verify response
        String paymentJson = """
                {
                    "paymentId": "1",
                    "amount": 100.0,
                    "currency": "USD",
                    "userId": "user123"
                }
                """;

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paymentJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("1"))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.amount").value(100.0));


        // Verify that the message producer was called with the correct argument
        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentMessageProducer).sendPayment(paymentArgumentCaptor.capture());

        Payment capturedPayment = paymentArgumentCaptor.getValue();
        assert capturedPayment.getPaymentId().equals("1");
        assert capturedPayment.getCurrency().equals("USD");
        assert capturedPayment.getUserId().equals("user123");
    }
}
