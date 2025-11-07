package org.springframework.samples.petclinic.appointments.app;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.samples.petclinic.appointments.api.AppointmentCreateCommand;
import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;
import org.springframework.samples.petclinic.appointments.api.AppointmentUpdateCommand;
import org.springframework.samples.petclinic.appointments.api.AppointmentView;
import org.springframework.samples.petclinic.appointments.api.AppointmentsFacade;
import org.springframework.samples.petclinic.appointments.domain.Appointment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AppointmentsFacadeImpl implements AppointmentsFacade {

    private final AppointmentService appointmentService;

    @Override
    @Transactional
    public AppointmentView create(AppointmentCreateCommand command) {
        Objects.requireNonNull(command.ownerId(), "Owner id is required");
        Objects.requireNonNull(command.petId(), "Pet id is required");
        Objects.requireNonNull(command.startTime(), "Start time is required");

        Appointment appointment = new Appointment();
        appointment.setOwnerId(command.ownerId());
        appointment.setPetId(command.petId());
        appointment.setVetId(command.vetId());
        appointment.setStartTime(command.startTime());
        appointment.setStatus(command.status() != null ? command.status() : AppointmentStatus.PENDING);
        appointment.setNotes(command.notes());

        return toView(appointmentService.save(appointment));
    }

    @Override
    public List<AppointmentView> findAll() {
        return appointmentService.findAll().stream()
            .map(this::toView)
            .toList();
    }

    @Override
    public List<AppointmentView> findByOwnerId(Integer ownerId) {
        return appointmentService.findByOwnerId(ownerId).stream()
            .map(this::toView)
            .toList();
    }

    @Override
    public List<AppointmentView> findByVetId(Integer vetId) {
        return appointmentService.findByVetId(vetId).stream()
            .map(this::toView)
            .toList();
    }

    @Override
    public Optional<AppointmentView> findById(Integer appointmentId) {
        return appointmentService.findById(appointmentId).map(this::toView);
    }

    @Override
    public Optional<AppointmentView> findForOwner(Integer appointmentId, Integer ownerId) {
        return appointmentService.findByIdAndOwnerId(appointmentId, ownerId)
            .map(this::toView);
    }

    @Override
    public Optional<AppointmentView> findForVet(Integer appointmentId, Integer vetId) {
        return appointmentService.findByIdAndVetId(appointmentId, vetId)
            .map(this::toView);
    }

    @Override
    @Transactional
    public Optional<AppointmentView> update(Integer appointmentId, AppointmentUpdateCommand command) {
        return appointmentService.findById(appointmentId)
            .map(existing -> {
                if (command.startTime() != null) {
                    existing.setStartTime(command.startTime());
                }
                if (command.status() != null) {
                    existing.setStatus(command.status());
                }
                if (command.notes() != null) {
                    existing.setNotes(command.notes());
                }
                if (command.vetId() != null) {
                    existing.setVetId(command.vetId());
                }
                return toView(appointmentService.save(existing));
            });
    }

    @Override
    @Transactional
    public boolean delete(Integer appointmentId) {
        return appointmentService.findById(appointmentId)
            .map(existing -> {
                appointmentService.delete(existing);
                return true;
            })
            .orElse(false);
    }

    private AppointmentView toView(Appointment appointment) {
        LocalDateTime createdAt = appointment.getCreatedAt();
        LocalDateTime updatedAt = appointment.getUpdatedAt();
        return new AppointmentView(
            appointment.getId(),
            appointment.getOwnerId(),
            appointment.getPetId(),
            appointment.getVetId(),
            appointment.getStartTime(),
            appointment.getStatus(),
            appointment.getNotes(),
            createdAt,
            updatedAt
        );
    }
}
