package org.springframework.samples.petclinic.appointments.web;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.appointments.api.AppointmentCreateCommand;
import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;
import org.springframework.samples.petclinic.appointments.api.AppointmentUpdateCommand;
import org.springframework.samples.petclinic.appointments.api.AppointmentView;
import org.springframework.samples.petclinic.appointments.api.AppointmentsFacade;
import org.springframework.samples.petclinic.appointments.app.workflow.AppointmentWorkflowService;
import org.springframework.samples.petclinic.appointments.web.dto.AppointmentAdminDto;
import org.springframework.samples.petclinic.appointments.web.dto.AppointmentAdminRequest;
import org.springframework.samples.petclinic.appointments.web.dto.AppointmentConfirmationRequest;
import org.springframework.samples.petclinic.appointments.web.dto.AppointmentUpdateRequest;
import org.springframework.samples.petclinic.appointments.web.dto.AppointmentVisitRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
public class AppointmentRestController {

    private final AppointmentsFacade appointmentsFacade;
    private final AppointmentWorkflowService appointmentWorkflowService;

    @GetMapping
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
    public ResponseEntity<List<AppointmentAdminDto>> listAppointments() {
        List<AppointmentAdminDto> body = appointmentsFacade.findAll().stream()
                .map(this::toDto)
                .toList();
        if (body.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/queue")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
    public ResponseEntity<List<AppointmentAdminDto>> getQueue(
            @RequestParam(name = "status", required = false) List<AppointmentStatus> statuses) {
        List<AppointmentAdminDto> body = appointmentWorkflowService.findQueue(statuses).stream()
                .map(this::toDto)
                .toList();
        if (body.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/{appointmentId}")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
    public ResponseEntity<AppointmentAdminDto> getAppointment(@PathVariable Integer appointmentId) {
        return appointmentsFacade.findById(appointmentId)
                .map(view -> new ResponseEntity<>(toDto(view), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
    public ResponseEntity<AppointmentAdminDto> createAppointment(@Valid @RequestBody AppointmentAdminRequest request) {
        AppointmentCreateCommand command = new AppointmentCreateCommand(
                request.ownerId(),
                request.petId(),
                request.vetId(),
                request.startTime(),
                request.status(),
                request.notes(),
                request.triageNotes());
        AppointmentView created = appointmentsFacade.create(command);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(
                UriComponentsBuilder.fromPath("/api/appointments/{id}")
                        .buildAndExpand(created.id())
                        .toUri());
        return new ResponseEntity<>(toDto(created), headers, HttpStatus.CREATED);
    }

    @PutMapping("/{appointmentId}")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
    public ResponseEntity<AppointmentAdminDto> updateAppointment(
            @PathVariable Integer appointmentId,
            @Valid @RequestBody AppointmentUpdateRequest request) {
        AppointmentUpdateCommand command = new AppointmentUpdateCommand(
                request.startTime(),
                request.status(),
                request.notes(),
                request.vetId(),
                request.triageNotes(),
                request.visitId());
        return appointmentsFacade.update(appointmentId, command)
                .map(view -> new ResponseEntity<>(toDto(view), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{appointmentId}")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Integer appointmentId) {
        boolean deleted = appointmentsFacade.delete(appointmentId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{appointmentId}/confirm")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
    public ResponseEntity<AppointmentAdminDto> confirmAppointment(
            @PathVariable Integer appointmentId,
            @Valid @RequestBody AppointmentConfirmationRequest request) {
        AppointmentView updated = appointmentWorkflowService.confirm(appointmentId, request.toCommand());
        return new ResponseEntity<>(toDto(updated), HttpStatus.OK);
    }

    @PostMapping("/{appointmentId}/visits")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
    public ResponseEntity<AppointmentAdminDto> createVisitFromAppointment(
            @PathVariable Integer appointmentId,
            @Valid @RequestBody AppointmentVisitRequest request) {
        AppointmentView updated = appointmentWorkflowService.createVisit(appointmentId, request.toCommand());
        return new ResponseEntity<>(toDto(updated), HttpStatus.OK);
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
                view.updatedAt(),
                view.triageNotes(),
                view.visitId());
    }

    public ResponseEntity<String> fallbackMethod(Throwable t) {
        return new ResponseEntity<>("Service temporarily unavailable. Please try again later.",
                HttpStatus.SERVICE_UNAVAILABLE);
    }
}
