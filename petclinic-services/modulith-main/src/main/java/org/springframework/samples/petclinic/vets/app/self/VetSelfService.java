package org.springframework.samples.petclinic.vets.app.self;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.appointments.api.AppointmentUpdateCommand;
import org.springframework.samples.petclinic.appointments.api.AppointmentView;
import org.springframework.samples.petclinic.appointments.api.AppointmentsFacade;
import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;
import org.springframework.samples.petclinic.vets.app.VetService;
import org.springframework.samples.petclinic.vets.domain.Vet;
import org.springframework.samples.petclinic.visits.api.VisitUpdateCommand;
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.samples.petclinic.visits.api.VisitsFacade;
import org.springframework.samples.petclinic.visits.api.VisitStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VetSelfService {

    private final VetService vetService;
    private final AppointmentsFacade appointmentsFacade;
    private final VisitsFacade visitsFacade;

    public Vet requireCurrentVet(String username) {
        return vetService.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vet profile not found for current user."));
    }

    public List<AppointmentView> listAppointments(Vet vet) {
        Integer vetId = vet.getId();
        if (vetId == null) {
            return List.of();
        }
        return appointmentsFacade.findByVetId(vetId);
    }

    @Transactional
    public AppointmentView confirmAppointment(Vet vet, Integer appointmentId) {
        Integer vetId = requireVetId(vet);
        AppointmentView appointment = appointmentsFacade.findForVet(appointmentId, vetId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment is not assigned to the current vet."));

        if (appointment.status() == AppointmentStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot confirm a cancelled appointment.");
        }
        if (appointment.status() == AppointmentStatus.COMPLETED) {
            return appointment;
        }
        if (appointment.status() == AppointmentStatus.CONFIRMED) {
            return appointment;
        }
        if (appointment.startTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot confirm an appointment in the past.");
        }

        AppointmentUpdateCommand command = new AppointmentUpdateCommand(
            null,
            AppointmentStatus.CONFIRMED,
            appointment.notes(),
            vetId,
            null,
            null
        );
        return appointmentsFacade.update(appointment.id(), command)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to confirm appointment."));
    }

    @Transactional
    public VisitView completeVisit(Vet vet, Integer visitId) {
        Integer vetId = requireVetId(vet);
        VisitView visit = visitsFacade.findById(visitId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visit not found."));

        if (visit.vetId() != null && !visit.vetId().equals(vetId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Visit is assigned to a different veterinarian.");
        }

        if (visit.status() == VisitStatus.COMPLETED) {
            return visit;
        }

        VisitUpdateCommand command = new VisitUpdateCommand(null, null, VisitStatus.COMPLETED, vetId);
        return visitsFacade.updateVisit(visitId, command)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to complete visit."));
    }

    private Integer requireVetId(Vet vet) {
        Integer vetId = vet.getId();
        if (vetId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vet profile is missing an identifier.");
        }
        return vetId;
    }
}
