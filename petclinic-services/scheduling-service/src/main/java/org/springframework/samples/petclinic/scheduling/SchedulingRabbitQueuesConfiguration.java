package org.springframework.samples.petclinic.scheduling;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.samples.petclinic.appointments.messaging.AppointmentMessagingProperties;

@Configuration
@EnableRabbit
@RequiredArgsConstructor
@ConditionalOnProperty(value = "petclinic.scheduling.service-enabled", havingValue = "true")
public class SchedulingRabbitQueuesConfiguration {

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
    public Declarables schedulingQueuesAndBindings(
            TopicExchange appointmentExchange,
            DirectExchange appointmentDeadLetterExchange) {
        // Queue chính: appointments.availability.q
        Queue availabilityQueue = QueueBuilder.durable(properties.getAvailabilityQueue())
                .withArgument("x-dead-letter-exchange", properties.getDeadLetterExchange())
                .withArgument("x-dead-letter-routing-key", properties.getAvailabilityDlq())
                .build();

        // DLQ: appointments.availability.dlq
        Queue availabilityDlq = QueueBuilder.durable(properties.getAvailabilityDlq()).build();

        // Binding từ exchange chính -> queue availability (routing key:
        // appointments.confirmed)
        Binding availabilityBinding = BindingBuilder.bind(availabilityQueue)
                .to(appointmentExchange)
                .with(properties.getConfirmedRoutingKey());

        // Binding từ DLX -> DLQ (routing key: appointments.availability.dlq)
        Binding availabilityDlqBinding = BindingBuilder.bind(availabilityDlq)
                .to(appointmentDeadLetterExchange)
                .with(properties.getAvailabilityDlq());

        return new Declarables(
                availabilityQueue,
                availabilityDlq,
                availabilityBinding,
                availabilityDlqBinding);
    }
}
