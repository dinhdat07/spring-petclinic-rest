package org.springframework.samples.petclinic.appointments.infra.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;

@Slf4j
class LoggingAppointmentAvailabilityProcessor implements AppointmentAvailabilityProcessor {

    @Override
    public void onAppointmentConfirmed(AppointmentConfirmedEvent event) {
        log.info("Availability placeholder - reserve slot for appointment {} (pet {}, vet {}).",
            event.appointmentId(), event.petId(), event.vetId());
    }
}
