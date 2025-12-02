package org.springframework.samples.petclinic.appointments.api;

import java.time.LocalDateTime;

import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;

public record AppointmentView(
    Integer id,
    Integer ownerId,
    Integer petId,
    Integer vetId,
    LocalDateTime startTime,
    AppointmentStatus status,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String triageNotes,
    Integer visitId
) {
}
