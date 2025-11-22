package org.springframework.samples.petclinic.appointments.infra.messaging;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;
import java.time.LocalDateTime;

import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.appointments.events.AppointmentVisitLinkedEvent;

@ExtendWith(MockitoExtension.class)
class AppointmentNotificationsListenerTests {

    @Mock
    private AppointmentNotificationsProcessor processor;

    @InjectMocks
    private AppointmentNotificationsListener listener;

    @Test
    void delegatesConfirmedEvents() {
        AppointmentConfirmedEvent event = new AppointmentConfirmedEvent(
            1, 2, 3, 4, AppointmentStatus.CONFIRMED, "triage", LocalDateTime.now(),
            "owner@example.com", "Owner Name", "vet@example.com", "Vet Name"
        );

        listener.handleAppointmentConfirmed(event);

        verify(processor).onAppointmentConfirmed(event);
    }

    @Test
    void rejectsWhenProcessorFails() {
        AppointmentVisitLinkedEvent event = new AppointmentVisitLinkedEvent(
            10, 20, 3, 4, 5, "owner@example.com", "Owner Name", "vet@example.com", "Vet Name"
        );
        doThrow(new IllegalStateException("boom")).when(processor).onVisitLinked(event);

        assertThatThrownBy(() -> listener.handleVisitLinked(event))
            .isInstanceOf(AmqpRejectAndDontRequeueException.class);
    }
}
