package org.springframework.samples.petclinic.owners.web.self;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.appointments.api.AppointmentView;
import org.springframework.samples.petclinic.catalog.api.PetTypeView;
import org.springframework.samples.petclinic.catalog.api.PetTypesFacade;
import org.springframework.samples.petclinic.owners.app.self.OwnerSelfService;
import org.springframework.samples.petclinic.owners.domain.Owner;
import org.springframework.samples.petclinic.owners.domain.Pet;
import org.springframework.samples.petclinic.owners.web.self.dto.OwnerAppointmentDto;
import org.springframework.samples.petclinic.owners.web.self.dto.OwnerAppointmentRequest;
import org.springframework.samples.petclinic.owners.web.self.dto.OwnerPetDto;
import org.springframework.samples.petclinic.owners.web.self.dto.OwnerPetVisitDto;
import org.springframework.samples.petclinic.owners.web.self.dto.OwnerProfileDto;
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.samples.petclinic.visits.api.VisitsFacade;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("/api/me")
@RequiredArgsConstructor
@PreAuthorize("hasRole(@roles.OWNER)")
public class OwnerSelfController {

    private final OwnerSelfService ownerSelfService;
    private final VisitsFacade visitsFacade;
    private final PetTypesFacade petTypesFacade;
    @GetMapping("/profile")
    public OwnerProfileDto profile(Authentication authentication) {
        Owner owner = ownerSelfService.requireCurrentOwner(authentication.getName());
        return toProfile(owner);
    }

    @GetMapping("/appointments")
    public List<OwnerAppointmentDto> appointments(Authentication authentication) {
        Owner owner = ownerSelfService.requireCurrentOwner(authentication.getName());
        return ownerSelfService.listAppointments(owner)
            .stream()
            .map(appointment -> toAppointmentDto(appointment, owner))
            .toList();
    }

    @PostMapping("/appointments")
    public ResponseEntity<OwnerAppointmentDto> createAppointment(
        Authentication authentication,
        @Valid @RequestBody OwnerAppointmentRequest request
    ) {
        Owner owner = ownerSelfService.requireCurrentOwner(authentication.getName());
        AppointmentView appointment = ownerSelfService.schedule(owner, request);
        OwnerAppointmentDto body = toAppointmentDto(appointment, owner);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(
            UriComponentsBuilder.fromPath("/api/me/appointments/{id}")
                .buildAndExpand(appointment.id())
                .toUri()
        );
        return new ResponseEntity<>(body, headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<Void> cancelAppointment(Authentication authentication, @PathVariable Integer appointmentId) {
        Owner owner = ownerSelfService.requireCurrentOwner(authentication.getName());
        ownerSelfService.cancel(owner, appointmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pets/{petId}/visits")
    public List<OwnerPetVisitDto> petVisits(Authentication authentication, @PathVariable Integer petId) {
        Owner owner = ownerSelfService.requireCurrentOwner(authentication.getName());
        Pet pet = owner.getPet(petId);
        if (pet == null) {
            return List.of();
        }
        return visitsFacade.findByPetId(petId).stream()
            .map(this::toVisitDto)
            .toList();
    }

    private OwnerProfileDto toProfile(Owner owner) {
        Map<Integer, String> typeNames = petTypesFacade.findAll().stream()
            .collect(Collectors.toMap(PetTypeView::id, PetTypeView::name));

        List<OwnerPetDto> pets = owner.getPets().stream()
            .filter(Objects::nonNull)
            .map(pet -> toPetDto(pet, typeNames))
            .toList();

        return new OwnerProfileDto(
            owner.getId(),
            owner.getUsername(),
            owner.getFirstName(),
            owner.getLastName(),
            owner.getAddress(),
            owner.getCity(),
            owner.getTelephone(),
            pets
        );
    }

    private OwnerAppointmentDto toAppointmentDto(AppointmentView appointment, Owner owner) {
        Pet pet = owner.getPet(appointment.petId());
        String petName = pet != null ? pet.getName() : null;
        String status = appointment.status() != null ? appointment.status().name() : null;
        return new OwnerAppointmentDto(
            appointment.id(),
            appointment.petId(),
            appointment.vetId(),
            petName,
            appointment.startTime(),
            status,
            appointment.notes(),
            appointment.createdAt(),
            appointment.updatedAt()
        );
    }

    private OwnerPetDto toPetDto(Pet pet, Map<Integer, String> typeNames) {
        List<OwnerPetVisitDto> visits = visitsFacade.findByPetId(pet.getId()).stream()
            .map(this::toVisitDto)
            .toList();

        return new OwnerPetDto(
            pet.getId(),
            pet.getName(),
            pet.getBirthDate(),
            pet.getTypeId(),
            typeNames.get(pet.getTypeId()),
            visits
        );
    }

    private OwnerPetVisitDto toVisitDto(VisitView view) {
        return new OwnerPetVisitDto(
            view.id(),
            view.date(),
            view.description(),
            view.status() != null ? view.status().name() : null
        );
    }
}
