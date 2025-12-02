package org.springframework.samples.petclinic.appointments.infra.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.samples.petclinic.appointments.messaging.AppointmentMessagingProperties;

@Configuration
@EnableRabbit
@EnableConfigurationProperties(AppointmentMessagingProperties.class)
public class AppointmentMessagingConfiguration {

    @Bean
    @ConditionalOnMissingBean(MessageConverter.class)
    MessageConverter appointmentMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    TopicExchange appointmentExchange(AppointmentMessagingProperties properties) {
        return new TopicExchange(properties.getExchange(), true, false);
    }

    @Bean
    DirectExchange appointmentDeadLetterExchange(AppointmentMessagingProperties properties) {
        return new DirectExchange(properties.getDeadLetterExchange(), true, false);
    }

    @Bean
    Declarables appointmentQueuesAndBindings(
        AppointmentMessagingProperties properties,
        TopicExchange appointmentExchange,
        DirectExchange appointmentDeadLetterExchange
    ) {
        Queue notificationsQueue = QueueBuilder.durable(properties.getNotificationsQueue())
            .withArgument("x-dead-letter-exchange", properties.getDeadLetterExchange())
            .withArgument("x-dead-letter-routing-key", properties.getNotificationsDlq())
            .build();

        Queue notificationsDlq = QueueBuilder.durable(properties.getNotificationsDlq()).build();

        Queue availabilityQueue = QueueBuilder.durable(properties.getAvailabilityQueue())
            .withArgument("x-dead-letter-exchange", properties.getDeadLetterExchange())
            .withArgument("x-dead-letter-routing-key", properties.getAvailabilityDlq())
            .build();

        Queue availabilityDlq = QueueBuilder.durable(properties.getAvailabilityDlq()).build();

        Binding notificationsConfirmedBinding = BindingBuilder.bind(notificationsQueue)
            .to(appointmentExchange)
            .with(properties.getConfirmedRoutingKey());

        Binding notificationsVisitBinding = BindingBuilder.bind(notificationsQueue)
            .to(appointmentExchange)
            .with(properties.getVisitLinkedRoutingKey());

        Binding availabilityConfirmedBinding = BindingBuilder.bind(availabilityQueue)
            .to(appointmentExchange)
            .with(properties.getConfirmedRoutingKey());

        Binding notificationsDlqBinding = BindingBuilder.bind(notificationsDlq)
            .to(appointmentDeadLetterExchange)
            .with(properties.getNotificationsDlq());

        Binding availabilityDlqBinding = BindingBuilder.bind(availabilityDlq)
            .to(appointmentDeadLetterExchange)
            .with(properties.getAvailabilityDlq());

        return new Declarables(
            notificationsQueue,
            notificationsDlq,
            availabilityQueue,
            availabilityDlq,
            notificationsConfirmedBinding,
            notificationsVisitBinding,
            availabilityConfirmedBinding,
            notificationsDlqBinding,
            availabilityDlqBinding
        );
    }

    @Bean
    @ConditionalOnMissingBean
    AppointmentNotificationsProcessor appointmentNotificationsProcessor() {
        return new LoggingAppointmentNotificationsProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    AppointmentAvailabilityProcessor appointmentAvailabilityProcessor() {
        return new LoggingAppointmentAvailabilityProcessor();
    }
}
