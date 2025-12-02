package org.springframework.samples.petclinic.owners.web.self.dto;

import java.time.LocalDate;

public record OwnerPetVisitDto(
    Integer id,
    LocalDate date,
    String description,
    String status
) {
}
