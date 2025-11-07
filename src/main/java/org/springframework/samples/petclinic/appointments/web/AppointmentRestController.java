package org.springframework.samples.petclinic.appointments.web;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.appointments.api.AppointmentCreateCommand;
import org.springframework.samples.petclinic.appointments.api.AppointmentUpdateCommand;
import org.springframework.samples.petclinic.appointments.api.AppointmentView;
import org.springframework.samples.petclinic.appointments.api.AppointmentsFacade;
import org.springframework.samples.petclinic.appointments.web.dto.AppointmentAdminDto;
import org.springframework.samples.petclinic.appointments.web.dto.AppointmentAdminRequest;
import org.springframework.samples.petclinic.appointments.web.dto.AppointmentUpdateRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
public class AppointmentRestController {

    private final AppointmentsFacade appointmentsFacade;

    @GetMapping
    public ResponseEntity<List<AppointmentAdminDto>> listAppointments() {
        List<AppointmentAdminDto> body = appointmentsFacade.findAll().stream()
            .map(this::toDto)
            .toList();
        if (body.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentAdminDto> getAppointment(@PathVariable Integer appointmentId) {
        return appointmentsFacade.findById(appointmentId)
            .map(view -> new ResponseEntity<>(toDto(view), HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<AppointmentAdminDto> createAppointment(@Valid @RequestBody AppointmentAdminRequest request) {
        AppointmentCreateCommand command = new AppointmentCreateCommand(
            request.ownerId(),
            request.petId(),
            request.vetId(),
            request.startTime(),
            request.status(),
            request.notes()
        );
        AppointmentView created = appointmentsFacade.create(command);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(
            UriComponentsBuilder.fromPath("/api/appointments/{id}")
                .buildAndExpand(created.id())
                .toUri()
        );
        return new ResponseEntity<>(toDto(created), headers, HttpStatus.CREATED);
    }

    @PutMapping("/{appointmentId}")
    public ResponseEntity<AppointmentAdminDto> updateAppointment(
        @PathVariable Integer appointmentId,
        @Valid @RequestBody AppointmentUpdateRequest request
    ) {
        AppointmentUpdateCommand command = new AppointmentUpdateCommand(
            request.startTime(),
            request.status(),
            request.notes(),
            request.vetId()
        );
        return appointmentsFacade.update(appointmentId, command)
            .map(view -> new ResponseEntity<>(toDto(view), HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Integer appointmentId) {
        boolean deleted = appointmentsFacade.delete(appointmentId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private AppointmentAdminDto toDto(AppointmentView view) {
        String status = view.status() != null ? view.status().name() : null;
        return new AppointmentAdminDto(
            view.id(),
            view.ownerId(),
            view.petId(),
            view.vetId(),
            view.startTime(),
            status,
            view.notes(),
            view.createdAt(),
            view.updatedAt()
        );
    }
}
