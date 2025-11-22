package org.springframework.samples.petclinic.appointments.infra.messaging;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import java.time.LocalDateTime;

import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.appointments.events.AppointmentVisitLinkedEvent;
import org.springframework.samples.petclinic.appointments.messaging.AppointmentMessagingProperties;

@ExtendWith(MockitoExtension.class)
class AppointmentEventAmqpAdapterTests {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private AppointmentMessagingProperties properties;

    private AppointmentEventAmqpAdapter adapter;

    @BeforeEach
    void setUp() {
        properties = new AppointmentMessagingProperties();
        adapter = new AppointmentEventAmqpAdapter(rabbitTemplate, properties);
    }

    @Test
    void publishesConfirmedAppointmentsToExchange() {
        var event = new AppointmentConfirmedEvent(
            1, 2, 3, 4, AppointmentStatus.CONFIRMED, "triage", LocalDateTime.now(),
            "owner@example.com", "Owner Name", "vet@example.com", "Vet Name"
        );

        adapter.onAppointmentConfirmed(event);

        verify(rabbitTemplate).convertAndSend(
            properties.getExchange(),
            properties.getConfirmedRoutingKey(),
            event
        );
    }

    @Test
    void publishesVisitLinkedEventsToExchange() {
        var event = new AppointmentVisitLinkedEvent(
            10, 20, 2, 3, 4, "owner@example.com", "Owner Name", "vet@example.com", "Vet Name"
        );

        adapter.onAppointmentVisitLinked(event);

        verify(rabbitTemplate).convertAndSend(
            properties.getExchange(),
            properties.getVisitLinkedRoutingKey(),
            event
        );
    }
}
