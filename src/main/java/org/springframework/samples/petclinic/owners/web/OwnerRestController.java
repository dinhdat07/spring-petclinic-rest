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
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.owners.api.OwnerApi;
import org.springframework.samples.petclinic.owners.app.owner.OwnerService;
import org.springframework.samples.petclinic.owners.domain.Owner;
import org.springframework.samples.petclinic.owners.mapper.OwnerMapper;
import org.springframework.samples.petclinic.owners.mapper.PetMapper;
import org.springframework.samples.petclinic.owners.web.dto.OwnerDto;
import org.springframework.samples.petclinic.owners.web.dto.OwnerFieldsDto;
import org.springframework.samples.petclinic.owners.web.dto.PetDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for owner related operations.
 */
@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("/api")
@RequiredArgsConstructor
public class OwnerRestController implements OwnerApi {

    private final OwnerService ownerService;
    private final OwnerMapper ownerMapper;
    private final PetMapper petMapper;

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
            .map(ownerMapper::toOwnerDto)
            .collect(Collectors.toList());
        return new ResponseEntity<>(ownerDtos, HttpStatus.OK);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<OwnerDto> getOwner(Integer ownerId) {
        return this.ownerService.findById(ownerId)
            .map(ownerMapper::toOwnerDto)
            .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<OwnerDto> addOwner(OwnerFieldsDto ownerFieldsDto) {
        HttpHeaders headers = new HttpHeaders();
        Owner owner = ownerMapper.toOwner(ownerFieldsDto);
        this.ownerService.save(owner);
        OwnerDto ownerDto = ownerMapper.toOwnerDto(owner);
        headers.setLocation(UriComponentsBuilder.newInstance()
            .path("/api/owners/{id}").buildAndExpand(owner.getId()).toUri());
        return new ResponseEntity<>(ownerDto, headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<OwnerDto> updateOwner(Integer ownerId, OwnerFieldsDto ownerFieldsDto) {
        return ownerService.findById(ownerId)
                .map(owner -> {
                    owner = ownerMapper.toOwner(ownerFieldsDto);
                    ownerService.save(owner);
                    return new ResponseEntity<>(ownerMapper.toOwnerDto(owner), HttpStatus.NO_CONTENT);
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

}

