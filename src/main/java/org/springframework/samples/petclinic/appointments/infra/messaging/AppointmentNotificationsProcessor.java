package org.springframework.samples.petclinic.appointments.infra.messaging;

import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.appointments.events.AppointmentVisitLinkedEvent;

/**
 * Handles notification side effects triggered by appointment lifecycle events.
 */
public interface AppointmentNotificationsProcessor {

    void onAppointmentConfirmed(AppointmentConfirmedEvent event);

    void onVisitLinked(AppointmentVisitLinkedEvent event);
}
