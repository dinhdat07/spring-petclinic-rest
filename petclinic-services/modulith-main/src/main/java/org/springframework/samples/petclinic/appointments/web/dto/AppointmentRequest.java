package org.springframework.samples.petclinic.appointments.web.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AppointmentRequest(
    @NotNull Integer petId,
    @NotNull @Future LocalDateTime startTime,
    @Size(max = 255) String notes
) {
}

