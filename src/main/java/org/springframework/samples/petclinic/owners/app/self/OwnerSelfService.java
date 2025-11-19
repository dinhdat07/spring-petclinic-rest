package org.springframework.samples.petclinic.owners.app.self;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.appointments.api.AppointmentCreateCommand;
import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;
import org.springframework.samples.petclinic.appointments.api.AppointmentUpdateCommand;
import org.springframework.samples.petclinic.appointments.api.AppointmentView;
import org.springframework.samples.petclinic.appointments.api.AppointmentsFacade;
import org.springframework.samples.petclinic.owners.web.self.dto.OwnerAppointmentRequest;
import org.springframework.samples.petclinic.owners.app.owner.OwnerService;
import org.springframework.samples.petclinic.owners.domain.Owner;
import org.springframework.samples.petclinic.owners.domain.Pet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OwnerSelfService {

    private static final Duration CANCELLATION_CUTOFF = Duration.ofHours(24);

    private final OwnerService ownerService;
    private final AppointmentsFacade appointmentsFacade;

    public Owner requireCurrentOwner(String username) {
        return ownerService.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner profile not found for current user."));
    }

    public List<AppointmentView> listAppointments(Owner owner) {
        return appointmentsFacade.findByOwnerId(owner.getId());
    }

    @Transactional
    public AppointmentView schedule(Owner owner, OwnerAppointmentRequest request) {
        Pet pet = owner.getPet(request.petId());
        if (pet == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pet does not belong to current owner.");
        }

        if (request.startTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment time must be in the future.");
        }

        return appointmentsFacade.create(new AppointmentCreateCommand(
            owner.getId(),
            pet.getId(),
            null,
            request.startTime(),
            AppointmentStatus.PENDING,
            request.notes(),
            null
        ));
    }

    @Transactional
    public AppointmentView cancel(Owner owner, Integer appointmentId) {
        AppointmentView appointment = appointmentsFacade.findForOwner(appointmentId, owner.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found."));

        if (appointment.status() == AppointmentStatus.CANCELLED) {
            return appointment;
        }
        if (appointment.status() == AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Completed appointments cannot be cancelled.");
        }

        if (appointment.status() == AppointmentStatus.CONFIRMED) {
            LocalDateTime now = LocalDateTime.now();
            if (!appointment.startTime().isAfter(now.plus(CANCELLATION_CUTOFF))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Confirmed appointments can only be cancelled 24h before start time.");
            }
        }

        if (appointment.startTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel past appointments.");
        }

        var updateCommand = new AppointmentUpdateCommand(
            null,
            AppointmentStatus.CANCELLED,
            appointment.notes(),
            appointment.vetId(),
            null,
            null
        );
        return appointmentsFacade.update(appointment.id(), updateCommand)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to cancel appointment."));
    }
}
