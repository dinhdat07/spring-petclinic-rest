package org.springframework.samples.petclinic.vets.web.self.dto;

import java.util.List;

public record VetProfileDto(
    Integer id,
    String username,
    String firstName,
    String lastName,
    List<String> specialties
) {
}

