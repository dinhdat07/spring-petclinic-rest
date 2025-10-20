package org.springframework.samples.petclinic.visits.api;

import java.time.LocalDate;

public record VisitView(
    int id,
    int petId,
    LocalDate date,
    String description
) {}
