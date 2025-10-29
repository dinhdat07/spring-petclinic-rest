package org.springframework.samples.petclinic.owners.web;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.catalog.api.PetTypeView;
import org.springframework.samples.petclinic.catalog.api.PetTypesFacade;
import org.springframework.samples.petclinic.owners.api.PetApi;
import org.springframework.samples.petclinic.owners.app.owner.OwnerService;
import org.springframework.samples.petclinic.owners.app.pet.PetService;
import org.springframework.samples.petclinic.owners.domain.Owner;
import org.springframework.samples.petclinic.owners.domain.Pet;
import org.springframework.samples.petclinic.owners.mapper.PetMapper;
import org.springframework.samples.petclinic.owners.web.dto.PetDto;
import org.springframework.samples.petclinic.owners.web.dto.PetFieldsDto;
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.samples.petclinic.visits.api.VisitsFacade;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;

/**
 * REST controller for pet resources.
 */
@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api")
@RequiredArgsConstructor
public class PetRestController implements PetApi {

    private final OwnerService ownerService;
    private final PetService petService;
    private final PetMapper petMapper;
    private final PetTypesFacade petTypesFacade;
    private final VisitsFacade visitsFacade;

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<PetDto> getPet(Integer petId) {
        return this.petService.findById(petId)
            .map(petMapper::toPetDto)
            .map(body -> new ResponseEntity<>(body, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<List<PetDto>> listPets() {
        List<PetDto> pets = this.petService.findAll().stream()
            .map(petMapper::toPetDto)
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
                    pet = petMapper.toPet(petDto);
                    petService.save(pet);
                return new ResponseEntity<>(petMapper.toPetDto(pet), HttpStatus.NO_CONTENT);
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
    

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<PetDto> addPetToOwner(Integer ownerId, PetFieldsDto petFieldsDto) {
        HttpHeaders headers = new HttpHeaders();
        Pet pet = petMapper.toPet(petFieldsDto);
        Owner owner = new Owner();
        owner.setId(ownerId);
        pet.setOwner(owner);
        
        petService.save(pet);
        PetDto petDto = petMapper.toPetDto(pet);
        headers.setLocation(UriComponentsBuilder.newInstance().path("/api/pets/{id}")
                .buildAndExpand(pet.getId()).toUri());
        return new ResponseEntity<>(petDto, headers, HttpStatus.CREATED);
    }
    
    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<PetDto> getOwnersPet(Integer ownerId, Integer petId) {
        return ownerService.findById(ownerId)
            .map(o -> o.getPet(petId))
            .map(pet -> new ResponseEntity<>(petMapper.toPetDto(pet), HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<Void> updateOwnersPet(Integer ownerId, Integer petId, PetFieldsDto petFieldsDto) {
        Optional<Owner> owner = this.ownerService.findById(ownerId);
        if (owner.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return this.petService.findById(petId)
            .map(existingPet -> {
                    existingPet = petMapper.toPet(petFieldsDto);
                    this.petService.save(existingPet);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
            })
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
