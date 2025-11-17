package org.springframework.samples.petclinic.appointments.infra.messaging;

import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;

/**
 * Handles asynchronous availability updates derived from appointment events.
 */
public interface AppointmentAvailabilityProcessor {

    void onAppointmentConfirmed(AppointmentConfirmedEvent event);
}
