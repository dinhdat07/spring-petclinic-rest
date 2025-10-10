package org.springframework.samples.petclinic.owners.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.catalog.api.PetTypeView;
import org.springframework.samples.petclinic.catalog.api.PetTypesFacade;
import org.springframework.samples.petclinic.owners.app.pet.PetService;
import org.springframework.samples.petclinic.owners.domain.Pet;
import org.springframework.samples.petclinic.owners.mapper.PetMapper;
import org.springframework.samples.petclinic.rest.api.PetsApi;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.samples.petclinic.visits.api.VisitsFacade;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for pet resources.
 */
@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api")
public class PetRestController implements PetsApi {

    private final PetService petService;
    private final PetMapper petMapper;
    private final PetTypesFacade petTypesFacade;
    private final VisitsFacade visitsFacade;

    public PetRestController(PetService petService,
                             PetMapper petMapper,
                             PetTypesFacade petTypesFacade,
                             VisitsFacade visitsFacade) {
        this.petService = petService;
        this.petMapper = petMapper;
        this.petTypesFacade = petTypesFacade;
        this.visitsFacade = visitsFacade;
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<PetDto> getPet(Integer petId) {
        return this.petService.findById(petId)
            .map(this::toPetDto)
            .map(body -> new ResponseEntity<>(body, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<List<PetDto>> listPets() {
        List<PetDto> pets = this.petService.findAll().stream()
            .map(this::toPetDto)
            .collect(Collectors.toList());
        if (pets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pets, HttpStatus.OK);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<PetDto> updatePet(Integer petId, PetDto petDto) {
        return this.petService.findById(petId)
            .map(pet -> {
                pet.setBirthDate(petDto.getBirthDate());
                pet.setName(petDto.getName());
                if (petDto.getType() != null) {
                    pet.setTypeId(petDto.getType().getId());
                }
                this.petService.save(pet);
                return new ResponseEntity<>(toPetDto(pet), HttpStatus.NO_CONTENT);
            })
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<PetDto> deletePet(Integer petId) {
        return this.petService.findById(petId)
            .map(pet -> {
                this.petService.delete(pet);
                return new ResponseEntity<PetDto>(HttpStatus.NO_CONTENT);
            })
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private PetDto toPetDto(Pet pet) {
        PetDto dto = petMapper.toPetDto(pet);
        dto.setId(pet.getId());
        dto.setOwnerId(pet.getOwner() != null ? pet.getOwner().getId() : null);
        dto.setType(resolvePetType(pet.getTypeId()));
        if (pet.getId() != null) {
            dto.setVisits(visitsFacade.findByPetId(pet.getId()).stream()
                .map(this::toVisitDto)
                .collect(Collectors.toList()));
        } else {
            dto.setVisits(List.of());
        }
        return dto;
    }

    private PetTypeDto resolvePetType(Integer typeId) {
        if (typeId == null) {
            return null;
        }
        return petTypesFacade.findById(typeId)
            .map(this::toPetTypeDto)
            .orElse(null);
    }

    private PetTypeDto toPetTypeDto(PetTypeView view) {
        PetTypeDto dto = new PetTypeDto();
        dto.setId(view.id());
        dto.setName(view.name());
        return dto;
    }

    private VisitDto toVisitDto(VisitView view) {
        VisitDto dto = new VisitDto();
        dto.setId(view.id());
        dto.setPetId(view.petId());
        dto.setDate(view.date());
        dto.setDescription(view.description());
        return dto;
    }
}
