package org.springframework.samples.petclinic.visits.api;

import java.time.LocalDate;

/**
 * Lightweight projection of a visit for cross-module interactions.
 */
public record VisitView(
    Integer id,
    Integer petId,
    LocalDate date,
    String description,
    VisitStatus status,
    Integer vetId
) {
}
