package org.springframework.samples.petclinic.scheduling.infra.messaging;

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
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.scheduling.domain.SchedulingAvailabilityService;

@ExtendWith(MockitoExtension.class)
class SchedulingEventListenerTests {

    @Mock
    private SchedulingAvailabilityService availabilityService;

    @InjectMocks
    private SchedulingEventListener listener;

    @Test
    void delegatesConfirmedEvent() {
        AppointmentConfirmedEvent event = new AppointmentConfirmedEvent(1, 2, 3, 5, AppointmentStatus.CONFIRMED, null);

        listener.handleAppointmentConfirmed(event);

        verify(availabilityService).onAppointmentConfirmed(event);
    }

    @Test
    void rejectsOnFailure() {
        AppointmentConfirmedEvent event = new AppointmentConfirmedEvent(1, 2, 3, 5, AppointmentStatus.CONFIRMED, null);
        doThrow(new IllegalStateException("boom")).when(availabilityService).onAppointmentConfirmed(event);

        assertThatThrownBy(() -> listener.handleAppointmentConfirmed(event))
            .isInstanceOf(AmqpRejectAndDontRequeueException.class);
    }
}
