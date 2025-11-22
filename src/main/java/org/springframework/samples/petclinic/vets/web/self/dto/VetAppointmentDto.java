package org.springframework.samples.petclinic.vets.web.self.dto;

import java.time.LocalDateTime;

public record VetAppointmentDto(
    Integer id,
    Integer petId,
    String petName,
    Integer ownerId,
    String ownerName,
    LocalDateTime startTime,
    String status,
    String notes
) {
}

