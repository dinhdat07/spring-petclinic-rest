package org.springframework.samples.petclinic.owners.web.self.dto;

import java.time.LocalDateTime;

public record OwnerAppointmentDto(
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

