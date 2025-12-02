package org.springframework.samples.petclinic.appointments.web.dto;

import java.time.LocalDateTime;

public record AppointmentDto(
    Integer id,
    Integer petId,
    Integer vetId,
    String petName,
    LocalDateTime startTime,
    String status,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
