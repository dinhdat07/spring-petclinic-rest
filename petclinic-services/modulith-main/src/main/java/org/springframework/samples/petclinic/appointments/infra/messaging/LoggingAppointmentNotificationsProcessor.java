package org.springframework.samples.petclinic.appointments.infra.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.appointments.events.AppointmentVisitLinkedEvent;

@Slf4j
class LoggingAppointmentNotificationsProcessor implements AppointmentNotificationsProcessor {

    @Override
    public void onAppointmentConfirmed(AppointmentConfirmedEvent event) {
        log.info("Notification placeholder - appointment {} confirmed for owner {} (pet {}, vet {}).",
            event.appointmentId(), event.ownerId(), event.petId(), event.vetId());
    }

    @Override
    public void onVisitLinked(AppointmentVisitLinkedEvent event) {
        log.info("Notification placeholder - appointment {} now has visit {} (pet {}, vet {}).",
            event.appointmentId(), event.visitId(), event.petId(), event.vetId());
    }
}
