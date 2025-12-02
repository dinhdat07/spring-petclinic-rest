package org.springframework.samples.petclinic.appointments.app;

import org.springframework.samples.petclinic.appointments.api.AppointmentView;
import org.springframework.samples.petclinic.appointments.domain.Appointment;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentView toView(Appointment appointment) {
        return new AppointmentView(
            appointment.getId(),
            appointment.getOwnerId(),
            appointment.getPetId(),
            appointment.getVetId(),
            appointment.getStartTime(),
            appointment.getStatus(),
            appointment.getNotes(),
            appointment.getCreatedAt(),
            appointment.getUpdatedAt(),
            appointment.getTriageNotes(),
            appointment.getVisitId()
        );
    }
}
