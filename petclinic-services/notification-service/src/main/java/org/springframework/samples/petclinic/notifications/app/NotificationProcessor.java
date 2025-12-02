package org.springframework.samples.petclinic.notifications.app;

import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.appointments.events.AppointmentVisitLinkedEvent;

public interface NotificationProcessor {

    void onAppointmentConfirmed(AppointmentConfirmedEvent event);

    void onVisitLinked(AppointmentVisitLinkedEvent event);
}
