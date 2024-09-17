package org.code.payment.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

@Configuration
@EnableJms
@Slf4j
public class ActiveMQConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory("tcp://localhost:61616");
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrency("1-1");
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(connectionFactory());
    }

    /*
    private PooledConnectionFactory pooledConnectionFactory(final ActiveMQConnectionFactory connectionFactory)  {
        try{
            PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
            pooledConnectionFactory.setConnectionFactory(connectionFactory);
            pooledConnectionFactory.setMaxConnections(2); // make this configurable
            return pooledConnectionFactory;
        }catch(Exception e){
            log.error("Unable to get create PooledConnectionFactory" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    */
}
