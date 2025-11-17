package org.springframework.samples.petclinic.appointments.web.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.samples.petclinic.appointments.app.workflow.AppointmentVisitCommand;

public record AppointmentVisitRequest(
    LocalDate date,
    @NotBlank String description,
    Integer vetId,
    @Size(max = 255) String triageNotes
) {

    public AppointmentVisitCommand toCommand() {
        return new AppointmentVisitCommand(date, description, vetId, triageNotes);
    }
}
