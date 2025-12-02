package org.springframework.samples.petclinic.owners.web.self.dto;

import java.util.List;

public record OwnerProfileDto(
    Integer id,
    String username,
    String firstName,
    String lastName,
    String address,
    String city,
    String telephone,
    List<OwnerPetDto> pets
) {
}

