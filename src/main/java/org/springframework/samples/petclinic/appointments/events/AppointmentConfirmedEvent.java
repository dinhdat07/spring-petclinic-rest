package org.springframework.samples.petclinic.appointments.events;

import java.time.LocalDateTime;

import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;

public record AppointmentConfirmedEvent(
    Integer appointmentId,
    Integer ownerId,
    Integer petId,
    Integer vetId,
    AppointmentStatus status,
    String triageNotes,
    LocalDateTime startTime
) {
}
