package org.springframework.samples.petclinic.vets.web;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.catalog.api.SpecialtiesFacade;
import org.springframework.samples.petclinic.catalog.api.SpecialtyView;
import org.springframework.samples.petclinic.vets.api.VetApi;
import org.springframework.samples.petclinic.vets.app.VetService;
import org.springframework.samples.petclinic.vets.domain.Vet;
import org.springframework.samples.petclinic.vets.mapper.VetMapper;
import org.springframework.samples.petclinic.vets.web.dto.SpecialtyDto;
import org.springframework.samples.petclinic.vets.web.dto.VetDto;
import org.springframework.samples.petclinic.vets.web.dto.VetDetailsDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.transaction.Transactional;

/**
 * REST controller for vets.
 */
@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api")
public class VetRestController implements VetApi {

    private final VetService vetService;
    private final SpecialtiesFacade specialtiesFacade;
    private final VetMapper vetMapper;

    public VetRestController(VetService vetService,
                             SpecialtiesFacade specialtiesFacade,
                             VetMapper vetMapper) {
        this.vetService = vetService;
        this.specialtiesFacade = specialtiesFacade;
        this.vetMapper = vetMapper;
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<List<VetDto>> listVets() {
        List<VetDto> vets = this.vetService.findAll().stream()
            .map(this::toVetDto)
            .collect(Collectors.toList());
        if (vets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(vets, HttpStatus.OK);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<VetDto> getVet(Integer vetId) {
        return this.vetService.findById(vetId)
            .map(this::toVetDto)
            .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<VetDto> addVet(VetDto vetDto) {
        HttpHeaders headers = new HttpHeaders();
        Vet vet = vetMapper.toVet(vetDto);
        vet.setSpecialtyIds(new HashSet<>(vetDto.getSpecialtyIds()));
        this.vetService.save(vet);
        VetDto response = toVetDto(vet);
        headers.setLocation(UriComponentsBuilder.newInstance().path("/api/vets/{id}")
            .buildAndExpand(vet.getId()).toUri());
        return new ResponseEntity<>(response, headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<VetDto> updateVet(Integer vetId, VetDto vetDto) {
        return this.vetService.findById(vetId)
            .map(existing -> {
                existing = vetMapper.toVet(vetDto);
                existing.setSpecialtyIds(new HashSet<>(vetDto.getSpecialtyIds()));
                this.vetService.save(existing);
                return new ResponseEntity<>(toVetDto(existing), HttpStatus.NO_CONTENT);
            })
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Transactional
    @Override
    public ResponseEntity<VetDto> deleteVet(Integer vetId) {
        return this.vetService.findById(vetId)
            .map(existing -> {
                this.vetService.delete(existing);
                return new ResponseEntity<VetDto>(HttpStatus.NO_CONTENT);
            })
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private VetDto toVetDto(Vet vet) {
        VetDto base = vetMapper.toVetDto(vet);
        base.setId(vet.getId());
        Set<Integer> specialtyIds = vet.getSpecialtyIds();
        base.setSpecialtyIds(specialtyIds.stream().collect(Collectors.toList()));

        VetDetailsDto detailed = new VetDetailsDto(base);
        if (!specialtyIds.isEmpty()) {
            List<SpecialtyDto> specialties = specialtiesFacade.findByIds(specialtyIds).stream()
                .map(this::toSpecialtyDto)
                .collect(Collectors.toList());
            detailed.setSpecialties(specialties);
        }

        return detailed;
    }

    private SpecialtyDto toSpecialtyDto(SpecialtyView view) {
        SpecialtyDto dto = new SpecialtyDto();
        dto.setId(view.id());
        dto.setName(view.name());
        return dto;
    }
}



