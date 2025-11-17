package org.springframework.samples.petclinic.appointments.app.workflow;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;
import org.springframework.samples.petclinic.appointments.api.AppointmentView;
import org.springframework.samples.petclinic.appointments.app.AppointmentMapper;
import org.springframework.samples.petclinic.appointments.app.AppointmentService;
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.appointments.events.AppointmentVisitLinkedEvent;
import org.springframework.samples.petclinic.appointments.domain.Appointment;
import org.springframework.samples.petclinic.visits.api.VisitCreateCommand;
import org.springframework.samples.petclinic.visits.api.VisitUpdateCommand;
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.samples.petclinic.visits.api.VisitsFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AppointmentWorkflowService {

    private final AppointmentService appointmentService;
    private final VisitsFacade visitsFacade;
    private final AppointmentMapper appointmentMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    public List<AppointmentView> findQueue(List<AppointmentStatus> statuses) {
        List<AppointmentStatus> effectiveStatuses = statuses == null
            ? List.of(AppointmentStatus.PENDING)
            : statuses.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (effectiveStatuses.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one status is required.");
        }

        return appointmentService.findByStatuses(effectiveStatuses).stream()
            .map(appointmentMapper::toView)
            .toList();
    }

    @Transactional
    public AppointmentView confirm(Integer appointmentId, AppointmentConfirmationCommand command) {
        Appointment appointment = appointmentService.findById(appointmentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found."));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cancelled appointments cannot be confirmed.");
        }
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            return appointmentMapper.toView(appointment);
        }
        if (appointment.getStatus() == AppointmentStatus.CONFIRMED) {
            // Allow updating vet or notes while already confirmed
            applyTriageInfo(appointment, command);
            Appointment saved = appointmentService.save(appointment);
            return appointmentMapper.toView(saved);
        }
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending appointments can be confirmed.");
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        applyTriageInfo(appointment, command);

        Appointment saved = appointmentService.save(appointment);
        AppointmentView view = appointmentMapper.toView(saved);
        applicationEventPublisher.publishEvent(
            new AppointmentConfirmedEvent(
                view.id(),
                view.ownerId(),
                view.petId(),
                view.vetId(),
                view.status(),
                view.triageNotes()
            )
        );
        return view;
    }

    @Transactional
    public AppointmentView createVisit(Integer appointmentId, AppointmentVisitCommand command) {
        Appointment appointment = appointmentService.findById(appointmentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found."));

        if (appointment.getVisitId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A visit already exists for this appointment.");
        }
        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only confirmed appointments can be converted into visits.");
        }
        if (command.description() == null || command.description().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Visit description is required.");
        }

        LocalDate visitDate = command.date() != null
            ? command.date()
            : appointment.getStartTime().toLocalDate();

        VisitCreateCommand visitCreateCommand = new VisitCreateCommand(
            appointment.getPetId(),
            visitDate,
            command.description()
        );

        VisitView visit = visitsFacade.createVisit(visitCreateCommand);
        VisitView assignedVisit = visit;
        if (command.vetId() != null) {
            assignedVisit = visitsFacade.updateVisit(
                visit.id(),
                new VisitUpdateCommand(null, null, null, command.vetId())
            ).orElse(visit);
        }

        appointment.setVisitId(assignedVisit.id());
        appointment.setStatus(AppointmentStatus.COMPLETED);
        if (command.vetId() != null) {
            appointment.setVetId(command.vetId());
        }
        if (command.triageNotes() != null && !command.triageNotes().isBlank()) {
            appointment.setTriageNotes(command.triageNotes());
        }

        Appointment saved = appointmentService.save(appointment);
        AppointmentView view = appointmentMapper.toView(saved);
        applicationEventPublisher.publishEvent(
            new AppointmentVisitLinkedEvent(
                view.id(),
                view.visitId(),
                view.ownerId(),
                view.petId(),
                view.vetId()
            )
        );
        return view;
    }

    private void applyTriageInfo(Appointment appointment, AppointmentConfirmationCommand command) {
        if (command == null) {
            return;
        }
        if (command.vetId() != null) {
            appointment.setVetId(command.vetId());
        }
        if (command.triageNotes() != null && !command.triageNotes().isBlank()) {
            appointment.setTriageNotes(command.triageNotes());
        }
    }
}
