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

@ExtendWith(MockitoExtension.class)
class AppointmentAvailabilityListenerTests {

    @Mock
    private AppointmentAvailabilityProcessor processor;

    @InjectMocks
    private AppointmentAvailabilityListener listener;

    @Test
    void delegatesAvailabilityUpdates() {
        AppointmentConfirmedEvent event = new AppointmentConfirmedEvent(
            42, 2, 3, 4, AppointmentStatus.CONFIRMED, "notes", LocalDateTime.now()
        );

        listener.handleAppointmentConfirmed(event);

        verify(processor).onAppointmentConfirmed(event);
    }

    @Test
    void routesFailuresToDlq() {
        AppointmentConfirmedEvent event = new AppointmentConfirmedEvent(
            42, 2, 3, 4, AppointmentStatus.CONFIRMED, "notes", LocalDateTime.now()
        );
        doThrow(new IllegalArgumentException("failure")).when(processor).onAppointmentConfirmed(event);

        assertThatThrownBy(() -> listener.handleAppointmentConfirmed(event))
            .isInstanceOf(AmqpRejectAndDontRequeueException.class);
    }
}
