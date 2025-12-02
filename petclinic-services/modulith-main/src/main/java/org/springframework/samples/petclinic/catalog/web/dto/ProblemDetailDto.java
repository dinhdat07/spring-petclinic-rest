package org.springframework.samples.petclinic.catalog.web.dto;

public record ProblemDetailDto(
    String type,
    String title,
    Integer status,
    String detail
) {
}

