package org.springframework.samples.petclinic.vets.web.self;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.samples.petclinic.appointments.api.AppointmentView;
import org.springframework.samples.petclinic.catalog.api.SpecialtiesFacade;
import org.springframework.samples.petclinic.catalog.api.SpecialtyView;
import org.springframework.samples.petclinic.owners.api.OwnerView;
import org.springframework.samples.petclinic.owners.api.OwnersFacade;
import org.springframework.samples.petclinic.owners.api.PetView;
import org.springframework.samples.petclinic.owners.api.PetsFacade;
import org.springframework.samples.petclinic.vets.app.self.VetSelfService;
import org.springframework.samples.petclinic.vets.domain.Vet;
import org.springframework.samples.petclinic.vets.web.self.dto.VetAppointmentDto;
import org.springframework.samples.petclinic.vets.web.self.dto.VetProfileDto;
import org.springframework.samples.petclinic.vets.web.self.dto.VetVisitDto;
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("/api/vets/me")
@RequiredArgsConstructor
@PreAuthorize("hasRole(@roles.VET)")
public class VetSelfController {

    private final VetSelfService vetSelfService;
    private final SpecialtiesFacade specialtiesFacade;
    private final PetsFacade petsFacade;
    private final OwnersFacade ownersFacade;

    @GetMapping("/profile")
    public VetProfileDto profile(Authentication authentication) {
        Vet vet = vetSelfService.requireCurrentVet(authentication.getName());
        List<String> specialties = vet.getSpecialtyIds() == null || vet.getSpecialtyIds().isEmpty()
            ? List.of()
            : specialtiesFacade.findByIds(vet.getSpecialtyIds()).stream()
                .filter(Objects::nonNull)
                .map(SpecialtyView::name)
                .collect(Collectors.toList());

        return new VetProfileDto(
            vet.getId(),
            vet.getUsername(),
            vet.getFirstName(),
            vet.getLastName(),
            specialties
        );
    }

    @GetMapping("/appointments")
    public List<VetAppointmentDto> appointments(Authentication authentication) {
        Vet vet = vetSelfService.requireCurrentVet(authentication.getName());
        return vetSelfService.listAppointments(vet).stream()
            .map(this::toAppointmentDto)
            .toList();
    }

    @PostMapping("/appointments/{appointmentId}/confirm")
    public VetAppointmentDto confirmAppointment(Authentication authentication, @PathVariable Integer appointmentId) {
        Vet vet = vetSelfService.requireCurrentVet(authentication.getName());
        AppointmentView updated = vetSelfService.confirmAppointment(vet, appointmentId);
        return toAppointmentDto(updated);
    }

    @PostMapping("/visits/{visitId}/complete")
    public VetVisitDto completeVisit(Authentication authentication, @PathVariable Integer visitId) {
        Vet vet = vetSelfService.requireCurrentVet(authentication.getName());
        VisitView visit = vetSelfService.completeVisit(vet, visitId);
        return toVisitDto(visit);
    }

    private VetAppointmentDto toAppointmentDto(AppointmentView view) {
        String petName = null;
        Integer ownerId = null;
        String ownerName = null;

        if (view.petId() != null) {
            PetView pet = petsFacade.findById(view.petId()).orElse(null);
            if (pet != null) {
                petName = pet.name();
                ownerId = pet.ownerId();
                if (ownerId != null) {
                    OwnerView owner = ownersFacade.findById(ownerId).orElse(null);
                    if (owner != null) {
                        ownerName = formatOwnerName(owner);
                    }
                }
            }
        }

        String status = view.status() != null ? view.status().name() : null;
        return new VetAppointmentDto(
            view.id(),
            view.petId(),
            petName,
            ownerId,
            ownerName,
            view.startTime(),
            status,
            view.notes()
        );
    }

    private VetVisitDto toVisitDto(VisitView visit) {
        return new VetVisitDto(
            visit.id(),
            visit.petId(),
            visit.date(),
            visit.description(),
            visit.status() != null ? visit.status().name() : null
        );
    }

    private String formatOwnerName(OwnerView owner) {
        String first = owner.firstName() != null ? owner.firstName() : "";
        String last = owner.secondName() != null ? owner.secondName() : "";
        return (first + " " + last).trim();
    }
}
