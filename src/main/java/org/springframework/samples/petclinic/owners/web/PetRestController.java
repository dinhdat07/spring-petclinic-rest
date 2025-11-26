package org.springframework.samples.petclinic.owners.web;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.owners.api.PetApi;
import org.springframework.samples.petclinic.owners.app.owner.OwnerService;
import org.springframework.samples.petclinic.owners.app.pet.PetService;
import org.springframework.samples.petclinic.owners.domain.Owner;
import org.springframework.samples.petclinic.owners.domain.Pet;
import org.springframework.samples.petclinic.owners.mapper.PetMapper;
import org.springframework.samples.petclinic.owners.web.dto.PetDto;
import org.springframework.samples.petclinic.owners.web.dto.PetFieldsDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
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
    private final PetDetailsAssembler petDetailsAssembler;

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
    @Override
    public ResponseEntity<PetDto> getPet(Integer petId) {
        return this.petService.findById(petId)
                .map(petDetailsAssembler::toDetailedDto)
                .map(body -> new ResponseEntity<>(body, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
    @Override
    public ResponseEntity<List<PetDto>> listPets() {
        List<PetDto> pets = this.petService.findAll().stream()
                .map(petDetailsAssembler::toDetailedDto)
                .collect(Collectors.toList());
        if (pets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pets, HttpStatus.OK);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
    @Override
    public ResponseEntity<PetDto> updatePet(Integer petId, PetDto petDto) {
        return this.petService.findById(petId)
                .map(pet -> {
                    pet = petMapper.toPet(petDto);
                    petService.save(pet);
                    return new ResponseEntity<>(petDetailsAssembler.toDetailedDto(pet), HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
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
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
    @Override
    public ResponseEntity<PetDto> addPetToOwner(Integer ownerId, PetFieldsDto petFieldsDto) {
        HttpHeaders headers = new HttpHeaders();
        Pet pet = petMapper.toPet(petFieldsDto);
        Owner owner = new Owner();
        owner.setId(ownerId);
        pet.setOwner(owner);

        petService.save(pet);
        PetDto petDto = petDetailsAssembler.toDetailedDto(pet);
        headers.setLocation(UriComponentsBuilder.newInstance().path("/api/pets/{id}")
                .buildAndExpand(pet.getId()).toUri());
        return new ResponseEntity<>(petDto, headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
    @Override
    public ResponseEntity<PetDto> getOwnersPet(Integer ownerId, Integer petId) {
        return ownerService.findById(ownerId)
                .map(o -> o.getPet(petId))
                .map(pet -> new ResponseEntity<>(petDetailsAssembler.toDetailedDto(pet), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
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

    public ResponseEntity<String> fallbackMethod(Throwable t) {
        return new ResponseEntity<>("Service temporarily unavailable. Please try again later.",
                HttpStatus.SERVICE_UNAVAILABLE);
    }

}
