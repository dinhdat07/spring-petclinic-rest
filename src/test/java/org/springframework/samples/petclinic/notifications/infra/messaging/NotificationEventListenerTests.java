package org.springframework.samples.petclinic.notifications.infra.messaging;

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
import org.springframework.samples.petclinic.notifications.app.NotificationProcessor;

@ExtendWith(MockitoExtension.class)
class NotificationEventListenerTests {

    @Mock
    private NotificationProcessor processor;

    @InjectMocks
    private NotificationEventListener listener;

    @Test
    void delegatesConfirmedEvents() {
        AppointmentConfirmedEvent event = new AppointmentConfirmedEvent(
            1, 2, 3, 4, AppointmentStatus.CONFIRMED, "notes", LocalDateTime.now()
        );

        listener.handleConfirmed(event);

        verify(processor).onAppointmentConfirmed(event);
    }

    @Test
    void routesFailuresToDlq() {
        AppointmentVisitLinkedEvent event = new AppointmentVisitLinkedEvent(1, 9, 2, 3, 4);
        doThrow(new IllegalStateException("boom")).when(processor).onVisitLinked(event);

        assertThatThrownBy(() -> listener.handleVisitLinked(event))
            .isInstanceOf(AmqpRejectAndDontRequeueException.class);
    }
}
