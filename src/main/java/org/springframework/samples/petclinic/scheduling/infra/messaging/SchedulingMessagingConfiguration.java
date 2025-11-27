package org.springframework.samples.petclinic.scheduling.infra.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.samples.petclinic.appointments.messaging.AppointmentMessagingProperties;
import org.springframework.samples.petclinic.scheduling.SchedulingServiceProperties;

@Configuration
@EnableRabbit
@ConditionalOnProperty(value = "petclinic.scheduling.service-enabled", havingValue = "true")
@EnableConfigurationProperties({AppointmentMessagingProperties.class, SchedulingServiceProperties.class})
public class SchedulingMessagingConfiguration {

    @Bean
    @ConditionalOnMissingBean(MessageConverter.class)
    MessageConverter schedulingMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(RabbitTemplate.class)
    RabbitTemplate schedulingRabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean(name = "rabbitListenerContainerFactory")
    @ConditionalOnMissingBean(name = "rabbitListenerContainerFactory")
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
        ConnectionFactory connectionFactory,
        MessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }
}
