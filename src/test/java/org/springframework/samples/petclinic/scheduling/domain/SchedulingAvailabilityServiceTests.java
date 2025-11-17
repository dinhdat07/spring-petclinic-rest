package org.springframework.samples.petclinic.scheduling.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;

class SchedulingAvailabilityServiceTests {

    @Test
    void incrementsCountsPerVet() {
        SchedulingAvailabilityService service = new SchedulingAvailabilityService();
        service.onAppointmentConfirmed(new AppointmentConfirmedEvent(1, 2, 3, 5, AppointmentStatus.CONFIRMED, null));
        service.onAppointmentConfirmed(new AppointmentConfirmedEvent(2, 2, 3, 5, AppointmentStatus.CONFIRMED, null));

        assertThat(service.activeAppointmentsForVet(5)).isEqualTo(2);
    }

    @Test
    void ignoresDuplicateEvents() {
        SchedulingAvailabilityService service = new SchedulingAvailabilityService();
        var event = new AppointmentConfirmedEvent(1, 2, 3, 5, AppointmentStatus.CONFIRMED, null);
        service.onAppointmentConfirmed(event);
        service.onAppointmentConfirmed(event);

        assertThat(service.activeAppointmentsForVet(5)).isEqualTo(1);
    }
}
