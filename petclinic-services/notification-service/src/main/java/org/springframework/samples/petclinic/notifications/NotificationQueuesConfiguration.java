package org.springframework.samples.petclinic.notifications;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.samples.petclinic.appointments.messaging.AppointmentMessagingProperties;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(value = "petclinic.notifications.service-enabled", havingValue = "true", matchIfMissing = true)
public class NotificationQueuesConfiguration {

    private final AppointmentMessagingProperties properties;

    @Bean
    public TopicExchange appointmentExchange() {
        // petclinic.appointments.exchange
        return new TopicExchange(properties.getExchange(), true, false);
    }

    @Bean
    public DirectExchange appointmentDeadLetterExchange() {
        // petclinic.appointments.dlx
        return new DirectExchange(properties.getDeadLetterExchange(), true, false);
    }

    @Bean
    public Declarables notificationQueuesAndBindings(
            TopicExchange appointmentExchange,
            DirectExchange appointmentDeadLetterExchange) {
        // Queue chính: appointments.notifications.q
        Queue notificationsQueue = QueueBuilder.durable(properties.getNotificationsQueue())
                .withArgument("x-dead-letter-exchange", properties.getDeadLetterExchange())
                .withArgument("x-dead-letter-routing-key", properties.getNotificationsDlq())
                .build();

        // DLQ: appointments.notifications.dlq
        Queue notificationsDlq = QueueBuilder.durable(properties.getNotificationsDlq()).build();

        // Binding từ exchange chính -> notificationsQueue
        // với các routing key:
        // - appointments.confirmed
        // - appointments.visit-linked
        Binding notificationsConfirmedBinding = BindingBuilder.bind(notificationsQueue)
                .to(appointmentExchange)
                .with(properties.getConfirmedRoutingKey());

        Binding notificationsVisitBinding = BindingBuilder.bind(notificationsQueue)
                .to(appointmentExchange)
                .with(properties.getVisitLinkedRoutingKey());

        // Binding từ DLX -> DLQ
        Binding notificationsDlqBinding = BindingBuilder.bind(notificationsDlq)
                .to(appointmentDeadLetterExchange)
                .with(properties.getNotificationsDlq());

        return new Declarables(
                notificationsQueue,
                notificationsDlq,
                notificationsConfirmedBinding,
                notificationsVisitBinding,
                notificationsDlqBinding);
    }
}
