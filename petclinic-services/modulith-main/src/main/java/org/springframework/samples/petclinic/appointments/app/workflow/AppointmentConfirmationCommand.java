package org.springframework.samples.petclinic.appointments.app.workflow;

public record AppointmentConfirmationCommand(
    String triageNotes,
    Integer vetId
) {
}
