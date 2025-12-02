package org.springframework.samples.petclinic.visits.api;

import java.time.LocalDate;

/**
 * Input payload for creating a visit.
 */
public record VisitCreateCommand(
    Integer petId,
    LocalDate date,
    String description
) {
}

