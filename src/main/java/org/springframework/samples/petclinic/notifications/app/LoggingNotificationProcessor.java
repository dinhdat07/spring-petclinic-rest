package org.springframework.samples.petclinic.notifications.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.appointments.events.AppointmentVisitLinkedEvent;

@Slf4j
public class LoggingNotificationProcessor implements NotificationProcessor {

    @Override
    public void onAppointmentConfirmed(AppointmentConfirmedEvent event) {
        log.info("NotificationService - would notify owner {} about appointment {} confirmation (pet {}, vet {}).",
            event.ownerId(), event.appointmentId(), event.petId(), event.vetId());
    }

    @Override
    public void onVisitLinked(AppointmentVisitLinkedEvent event) {
        log.info("NotificationService - would notify owner {} about visit {} linked to appointment {}.",
            event.ownerId(), event.visitId(), event.appointmentId());
    }
}
