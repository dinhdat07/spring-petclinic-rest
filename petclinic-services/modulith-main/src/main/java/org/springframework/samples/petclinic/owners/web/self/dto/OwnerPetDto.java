package org.springframework.samples.petclinic.owners.web.self.dto;

import java.time.LocalDate;
import java.util.List;

public record OwnerPetDto(
    Integer id,
    String name,
    LocalDate birthDate,
    Integer typeId,
    String typeName,
    List<OwnerPetVisitDto> visits
) {
}

