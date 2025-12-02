package org.springframework.samples.petclinic.notifications.infra.messaging;

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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.samples.petclinic.appointments.messaging.AppointmentMessagingProperties;
import org.springframework.samples.petclinic.notifications.NotificationServiceProperties;
import org.springframework.samples.petclinic.notifications.app.LoggingNotificationProcessor;
import org.springframework.samples.petclinic.notifications.app.NotificationProcessor;
import org.springframework.samples.petclinic.notifications.infra.email.EmailNotificationProcessor;
import org.springframework.samples.petclinic.notifications.infra.email.NotificationEmailProperties;
import org.springframework.samples.petclinic.notifications.infra.repository.NotificationLogRepository;

@Configuration
@EnableRabbit
@ConditionalOnProperty(value = "petclinic.notifications.service-enabled", havingValue = "true")
@EnableConfigurationProperties({
    AppointmentMessagingProperties.class,
    NotificationServiceProperties.class,
    NotificationEmailProperties.class
})
public class NotificationMessagingConfiguration {

    @Bean
    @ConditionalOnProperty(value = "petclinic.notifications.email.enabled", havingValue = "true")
    NotificationProcessor emailNotificationProcessor(
        JavaMailSender mailSender,
        NotificationEmailProperties emailProperties,
        NotificationLogRepository notificationLogRepository
    ) {
        return new EmailNotificationProcessor(mailSender, emailProperties, notificationLogRepository);
    }

    @Bean
    @ConditionalOnMissingBean(MessageConverter.class)
    MessageConverter notificationMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    RabbitTemplate notificationRabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
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

    @Bean
    @ConditionalOnMissingBean(NotificationProcessor.class)
    NotificationProcessor notificationProcessorFallback() {
        return new LoggingNotificationProcessor();
    }
}
