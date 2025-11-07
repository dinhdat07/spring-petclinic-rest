package org.springframework.samples.petclinic.appointments.api;

import java.time.LocalDateTime;

import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;

public record AppointmentUpdateCommand(
    LocalDateTime startTime,
    AppointmentStatus status,
    String notes,
    Integer vetId
) {
}
