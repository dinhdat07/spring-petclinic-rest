package org.springframework.samples.petclinic.appointments.api;

import java.time.LocalDateTime;

import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;

public record AppointmentCreateCommand(
    Integer ownerId,
    Integer petId,
    Integer vetId,
    LocalDateTime startTime,
    AppointmentStatus status,
    String notes,
    String triageNotes
) {
}
