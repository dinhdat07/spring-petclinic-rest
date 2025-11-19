package org.springframework.samples.petclinic.visits.api;

import java.time.LocalDate;

import org.springframework.samples.petclinic.visits.api.VisitStatus;

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
