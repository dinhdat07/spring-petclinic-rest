package org.springframework.samples.petclinic.appointments.web.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;

public record AppointmentAdminRequest(
    @NotNull Integer ownerId,
    @NotNull Integer petId,
    Integer vetId,
    @NotNull @Future LocalDateTime startTime,
    AppointmentStatus status,
    @Size(max = 255) String notes
) {
}
