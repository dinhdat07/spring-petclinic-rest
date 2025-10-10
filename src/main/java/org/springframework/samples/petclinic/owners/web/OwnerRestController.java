/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.owners.web;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.catalog.api.PetTypeView;
import org.springframework.samples.petclinic.catalog.api.PetTypesFacade;
import org.springframework.samples.petclinic.owners.app.owner.OwnerService;
import org.springframework.samples.petclinic.owners.app.pet.PetService;
import org.springframework.samples.petclinic.owners.domain.Owner;
import org.springframework.samples.petclinic.owners.domain.Pet;
import org.springframework.samples.petclinic.owners.mapper.OwnerMapper;
import org.springframework.samples.petclinic.owners.mapper.PetMapper;
import org.springframework.samples.petclinic.rest.api.OwnersApi;
import org.springframework.samples.petclinic.rest.dto.OwnerDto;
import org.springframework.samples.petclinic.rest.dto.OwnerFieldsDto;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.dto.PetFieldsDto;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.rest.dto.VisitFieldsDto;
import org.springframework.samples.petclinic.visits.api.VisitCreateCommand;
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.samples.petclinic.visits.api.VisitsFacade;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.transaction.Transactional;

/**
 * REST controller for owner related operations.
 */
@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("/api")
public class OwnerRestController implements OwnersApi {

    private final OwnerService ownerService;
    private final PetService petService;
    private final OwnerMapper ownerMapper;
    private final PetMapper petMapper;
    private final VisitsFacade visitsFacade;
    private final PetTypesFacade petTypesFacade;

    public OwnerRestController(OwnerService ownerService,
                               PetService petService,
                               OwnerMapper ownerMapper,
                               PetMapper petMapper,
                               VisitsFacade visitsFacade,
                               PetTypesFacade petTypesFacade) {
        this.ownerService = ownerService;
        this.petService = petService;
        this.ownerMapper = ownerMapper;
        this.petMapper = petMapper;
        this.visitsFacade = visitsFacade;
        this.petTypesFacade = petTypesFacade;
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<List<OwnerDto>> listOwners(String lastName) {
        Collection<Owner> owners = (lastName != null)
            ? this.ownerService.findByLastName(lastName)
            : this.ownerService.findAll();

        if (owners.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<OwnerDto> ownerDtos = owners.stream()
            .map(this::toOwnerDto)
            .collect(Collectors.toList());
        return new ResponseEntity<>(ownerDtos, HttpStatus.OK);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<OwnerDto> getOwner(Integer ownerId) {
        return this.ownerService.findById(ownerId)
            .map(this::toOwnerDto)
            .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<OwnerDto> addOwner(OwnerFieldsDto ownerFieldsDto) {
        HttpHeaders headers = new HttpHeaders();
        Owner owner = ownerMapper.toOwner(ownerFieldsDto);
        this.ownerService.save(owner);
        OwnerDto ownerDto = toOwnerDto(owner);
        headers.setLocation(UriComponentsBuilder.newInstance()
            .path("/api/owners/{id}").buildAndExpand(owner.getId()).toUri());
        return new ResponseEntity<>(ownerDto, headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<OwnerDto> updateOwner(Integer ownerId, OwnerFieldsDto ownerFieldsDto) {
        return this.ownerService.findById(ownerId)
            .map(owner -> {
                owner.setAddress(ownerFieldsDto.getAddress());
                owner.setCity(ownerFieldsDto.getCity());
                owner.setFirstName(ownerFieldsDto.getFirstName());
                owner.setLastName(ownerFieldsDto.getLastName());
                owner.setTelephone(ownerFieldsDto.getTelephone());
                this.ownerService.save(owner);
                return new ResponseEntity<>(toOwnerDto(owner), HttpStatus.NO_CONTENT);
            })
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Transactional
    @Override
    public ResponseEntity<OwnerDto> deleteOwner(Integer ownerId) {
        return this.ownerService.findById(ownerId)
            .map(owner -> {
                this.ownerService.delete(owner);
                return new ResponseEntity<OwnerDto>(HttpStatus.NO_CONTENT);
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
        this.petService.save(pet);
        PetDto petDto = toPetDto(pet);
        headers.setLocation(UriComponentsBuilder.newInstance().path("/api/pets/{id}")
            .buildAndExpand(pet.getId()).toUri());
        return new ResponseEntity<>(petDto, headers, HttpStatus.CREATED);
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
                existingPet.setBirthDate(petFieldsDto.getBirthDate());
                existingPet.setName(petFieldsDto.getName());
                existingPet.setTypeId(petFieldsDto.getType().getId());
                this.petService.save(existingPet);
                return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
            })
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<VisitDto> addVisitToOwner(Integer ownerId, Integer petId, VisitFieldsDto visitFieldsDto) {
        HttpHeaders headers = new HttpHeaders();
        VisitView visit = this.visitsFacade.createVisit(
            new VisitCreateCommand(petId, visitFieldsDto.getDate(), visitFieldsDto.getDescription()));
        VisitDto visitDto = toVisitDto(visit);
        headers.setLocation(UriComponentsBuilder.newInstance().path("/api/visits/{id}")
            .buildAndExpand(visit.id()).toUri());
        return new ResponseEntity<>(visitDto, headers, HttpStatus.CREATED);
    }


    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<PetDto> getOwnersPet(Integer ownerId, Integer petId) {
        return this.ownerService.findById(ownerId)
            .map(o -> o.getPet(petId))
            .map(pet -> new ResponseEntity<>(toPetDto(pet), HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private OwnerDto toOwnerDto(Owner owner) {
        OwnerDto dto = ownerMapper.toOwnerDto(owner);
        List<PetDto> pets = owner.getPets().stream()
            .map(this::toPetDto)
            .collect(Collectors.toList());
        dto.setPets(pets);
        return dto;
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

