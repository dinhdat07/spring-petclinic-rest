package org.springframework.samples.petclinic.visits.web.dto;

public record ProblemDetailDto(
    Integer status,
    String title,
    String detail
) {
}
