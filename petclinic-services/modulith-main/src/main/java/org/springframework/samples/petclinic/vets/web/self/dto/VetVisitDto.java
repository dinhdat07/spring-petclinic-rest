package org.springframework.samples.petclinic.vets.web.self.dto;

import java.time.LocalDate;

public record VetVisitDto(
    Integer id,
    Integer petId,
    LocalDate date,
    String description,
    String status
) {
}

