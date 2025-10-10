package org.springframework.samples.petclinic.vets.web;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.catalog.api.SpecialtiesFacade;
import org.springframework.samples.petclinic.catalog.api.SpecialtyView;
import org.springframework.samples.petclinic.rest.api.VetsApi;
import org.springframework.samples.petclinic.rest.dto.SpecialtyDto;
import org.springframework.samples.petclinic.rest.dto.VetDto;
import org.springframework.samples.petclinic.vets.app.VetService;
import org.springframework.samples.petclinic.vets.domain.Vet;
import org.springframework.samples.petclinic.vets.mapper.VetMapper;
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
public class VetRestController implements VetsApi {

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
        vet.setSpecialtyIds(extractSpecialtyIds(vetDto));
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
                existing.setFirstName(vetDto.getFirstName());
                existing.setLastName(vetDto.getLastName());
                existing.setSpecialtyIds(extractSpecialtyIds(vetDto));
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
        VetDto dto = vetMapper.toVetDto(vet);
        dto.setId(vet.getId());
        dto.setSpecialties(resolveSpecialties(vet.getSpecialtyIds()));
        return dto;
    }

    private List<SpecialtyDto> resolveSpecialties(Set<Integer> specialtyIds) {
        if (specialtyIds == null || specialtyIds.isEmpty()) {
            return List.of();
        }
        return specialtiesFacade.findByIds(specialtyIds).stream()
            .map(this::toSpecialtyDto)
            .collect(Collectors.toList());
    }

    private Set<Integer> extractSpecialtyIds(VetDto vetDto) {
        if (vetDto.getSpecialties() == null) {
            return Set.of();
        }
        return vetDto.getSpecialties().stream()
            .map(SpecialtyDto::getId)
            .collect(Collectors.toSet());
    }

    private SpecialtyDto toSpecialtyDto(SpecialtyView view) {
        SpecialtyDto dto = new SpecialtyDto();
        dto.setId(view.id());
        dto.setName(view.name());
        return dto;
    }
}
