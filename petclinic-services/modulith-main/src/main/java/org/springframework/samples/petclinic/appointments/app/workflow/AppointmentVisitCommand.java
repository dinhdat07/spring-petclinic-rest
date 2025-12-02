package org.springframework.samples.petclinic.appointments.app.workflow;

import java.time.LocalDate;

public record AppointmentVisitCommand(
    LocalDate date,
    String description,
    Integer vetId,
    String triageNotes
) {
}
