package org.springframework.samples.petclinic.owners.web.dto;

public record ProblemDetailDto(
    String type,
    String title,
    Integer status,
    String detail
) {
}

