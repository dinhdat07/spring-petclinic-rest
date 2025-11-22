package org.springframework.samples.petclinic.appointments.events;

public record AppointmentVisitLinkedEvent(
    Integer appointmentId,
    Integer visitId,
    Integer ownerId,
    Integer petId,
    Integer vetId
) {
}
