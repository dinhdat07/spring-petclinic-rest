package org.springframework.samples.petclinic.visits.api;

import java.time.LocalDate;

/**
 * Input payload for updating visit details.
 */
public record VisitUpdateCommand(
    LocalDate date,
    String description,
    VisitStatus status,
    Integer vetId
) {
}

