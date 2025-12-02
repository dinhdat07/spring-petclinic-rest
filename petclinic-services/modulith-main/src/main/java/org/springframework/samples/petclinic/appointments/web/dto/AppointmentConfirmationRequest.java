package org.springframework.samples.petclinic.appointments.web.dto;

import jakarta.validation.constraints.Size;

import org.springframework.samples.petclinic.appointments.app.workflow.AppointmentConfirmationCommand;

public record AppointmentConfirmationRequest(
    @Size(max = 255) String triageNotes,
    Integer vetId
) {

    public AppointmentConfirmationCommand toCommand() {
        return new AppointmentConfirmationCommand(triageNotes, vetId);
    }
}
