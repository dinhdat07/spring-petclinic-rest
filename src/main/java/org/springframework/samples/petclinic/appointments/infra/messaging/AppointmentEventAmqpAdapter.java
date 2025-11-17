package org.springframework.samples.petclinic.appointments.infra.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.appointments.events.AppointmentVisitLinkedEvent;
import org.springframework.samples.petclinic.appointments.messaging.AppointmentMessagingProperties;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentEventAmqpAdapter {

    private final RabbitTemplate rabbitTemplate;
    private final AppointmentMessagingProperties properties;

    @EventListener
    public void onAppointmentConfirmed(AppointmentConfirmedEvent event) {
        publish(properties.getConfirmedRoutingKey(), event);
    }

    @EventListener
    public void onAppointmentVisitLinked(AppointmentVisitLinkedEvent event) {
        publish(properties.getVisitLinkedRoutingKey(), event);
    }

    private void publish(String routingKey, Object payload) {
        try {
            rabbitTemplate.convertAndSend(properties.getExchange(), routingKey, payload);
            log.debug("Published appointment event [{}] to exchange '{}' with routing key '{}'", payload, properties.getExchange(), routingKey);
        }
        catch (Exception ex) {
            log.error("Failed to publish appointment event {} via AMQP", payload, ex);
        }
    }
}
