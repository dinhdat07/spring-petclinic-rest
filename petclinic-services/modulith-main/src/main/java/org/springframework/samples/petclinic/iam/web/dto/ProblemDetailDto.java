package org.springframework.samples.petclinic.iam.web.dto;

public record ProblemDetailDto(
    String type,
    String title,
    Integer status,
    String detail
) {
}

