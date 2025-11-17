package org.springframework.samples.petclinic.appointments.web.dto;

import java.time.LocalDateTime;

public record AppointmentAdminDto(
    Integer id,
    Integer ownerId,
    Integer petId,
    Integer vetId,
    LocalDateTime startTime,
    String status,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String triageNotes,
    Integer visitId
) {
}
