package org.springframework.samples.petclinic.vets.web.dto;

public record ProblemDetailDto(
    String type,
    String title,
    Integer status,
    String detail
) {
}

