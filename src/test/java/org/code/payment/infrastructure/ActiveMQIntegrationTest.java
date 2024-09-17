package org.code.payment.infrastructure;

import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;

import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.code.payment.domain.Payment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ActiveMQIntegrationTest.ActiveMQConfigurationTest.class })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Enables @Order for method execution order
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)  // Ensures broker restarts for each test
class ActiveMQIntegrationTest {

    @Autowired
    private JmsTemplate jmsTemplate;

    public static EmbeddedActiveMQBroker embeddedActiveMQBroker;

    private final String testQueue = "test-queue";

    @BeforeAll
    public static void startEmbeddedActiveMQBroker() {
        System.out.println("Broker starting up");
        embeddedActiveMQBroker = new EmbeddedActiveMQBroker();
        embeddedActiveMQBroker.start();
    }

    @AfterAll
    public static void stopEmbeddedActiveMQBroker() {
        System.out.println("Broker stopping up");
        if (embeddedActiveMQBroker != null) {
            embeddedActiveMQBroker.stop();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test sending message to the queue")
    void testSendMessage() {
        Payment payment = new Payment("1", new BigDecimal("100.00"), "USD", "User123");
        jmsTemplate.convertAndSend(testQueue, payment);

        // Use a nested class for receiving messages in a second step
        MessageTests.receiveMessage(jmsTemplate, testQueue);
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Enables ordering within the nested class
    class MessageTests {

        @Test
        @Order(2)
        @DisplayName("Test receiving message from the queue")
        void testReceiveMessage() {
            Payment receivedPayment = (Payment) jmsTemplate.receiveAndConvert(testQueue);
            assert receivedPayment != null;
            assertEquals("USD", receivedPayment.getCurrency(), "Message received should match sent message");
        }

        static void receiveMessage(JmsTemplate jmsTemplate, String queueName) {
            Payment receivedPayment = (Payment) jmsTemplate.receiveAndConvert(queueName);
            assertNotNull(receivedPayment, "Received message should not be null");
            assertThat(receivedPayment.getAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
        }
    }

    @TestConfiguration
    @EnableJms
    public static class ActiveMQConfigurationTest {

        @Bean
        public ConnectionFactory connectionFactory() {
            System.out.println("Active MQ connection factory created");
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(embeddedActiveMQBroker.getVmURL());
            factory.setTrustAllPackages(true);
            return factory;
        }

        @Bean
        public JmsListenerContainerFactory<?> jmsListenerContainerFactory() {
            System.out.println("Active MQ listener factory created");
            DefaultJmsListenerContainerFactory jmsListenerContainerFactory = new DefaultJmsListenerContainerFactory();
            jmsListenerContainerFactory.setConnectionFactory(connectionFactory());
            jmsListenerContainerFactory.setConcurrency("1-1");
            return jmsListenerContainerFactory;
        }

        @Bean
        public JmsTemplate jmsTemplate() {
            System.out.println("Active MQ jmsTemplate created");
            JmsTemplate jmsTemplate = new JmsTemplate();
            jmsTemplate.setConnectionFactory(connectionFactory());
            return jmsTemplate;
        }
    }
}