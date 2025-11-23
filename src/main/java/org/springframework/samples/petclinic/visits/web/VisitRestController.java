package org.springframework.samples.petclinic.visits.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.catalog.api.PetTypesFacade;
import org.springframework.samples.petclinic.owners.api.OwnersFacade;
import org.springframework.samples.petclinic.owners.api.PetView;
import org.springframework.samples.petclinic.owners.api.PetsFacade;
import org.springframework.samples.petclinic.visits.api.VisitApi;
import org.springframework.samples.petclinic.visits.app.VisitService;
import org.springframework.samples.petclinic.visits.domain.Visit;
import org.springframework.samples.petclinic.visits.mapper.VisitMapper;
import org.springframework.samples.petclinic.visits.web.dto.VisitDto;
import org.springframework.samples.petclinic.visits.web.dto.VisitFieldsDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api")
@RequiredArgsConstructor
public class VisitRestController implements VisitApi {
    private final PetsFacade petsFacade;

    private final OwnersFacade ownersFacade;

    private final PetTypesFacade petTypesFacade;

    private final VisitService visitService;

    private final VisitMapper visitMapper;

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
    @Override
    public ResponseEntity<List<VisitDto>> listVisits() {
        List<VisitDto> visits = new ArrayList<>(
                visitService.findAll().stream().map(this::toDetailsDto).toList());
        if (visits.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(visits, HttpStatus.OK);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
    @Override
    public ResponseEntity<VisitDto> getVisit(Integer visitId) {
        Optional<VisitDto> dto = visitService.findById(visitId).map(this::toDetailsDto);

        return dto
                .map(d -> new ResponseEntity<>(d, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
    @Transactional
    @Override
    public ResponseEntity<VisitDto> addVisit(VisitDto visitDto) {
        // map DTO -> entity
        Visit visit = new Visit();
        visit.setPetId(visitDto.getPetId());
        visit.setDescription(visitDto.getDescription());
        if (visitDto.getDate() != null) {
            visit.setDate(visitDto.getDate());
        }

        // persist
        visitService.save(visit);

        // build response
        VisitDto responseBody = this.toDetailsDto(visit);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(
                UriComponentsBuilder.newInstance()
                        .path("/api/visits/{id}")
                        .buildAndExpand(responseBody.getId())
                        .toUri());
        return new ResponseEntity<>(responseBody, headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
    @Transactional
    @Override
    public ResponseEntity<VisitDto> updateVisit(Integer visitId, VisitFieldsDto visitDto) {
        Optional<VisitDto> updated = visitService.findById(visitId).map(existing -> {
            if (visitDto.getDate() != null) {
                existing.setDate(visitDto.getDate());
            }
            existing.setDescription(visitDto.getDescription());
            visitService.save(existing);
            return this.toDetailsDto(existing);
        });

        return updated
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
    @Transactional
    @Override
    public ResponseEntity<VisitDto> deleteVisit(Integer visitId) {
        return visitService.findById(visitId)
                .map(existing -> {
                    visitService.delete(existing);
                    return new ResponseEntity<VisitDto>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "fallbackMethod")
    @Retry(name = "myRetry")
    @Override
    public ResponseEntity<VisitDto> addVisitToOwner(Integer ownerId, Integer petId, VisitFieldsDto visitFieldsDto) {
        HttpHeaders headers = new HttpHeaders();

        Visit visit = new Visit();
        visit.setPetId(visitFieldsDto.getPetId());
        visit.setDescription(visitFieldsDto.getDescription());
        visit.setDate(visitFieldsDto.getDate());

        visitService.save(visit);

        VisitDto visitDto = this.toDetailsDto(visit);
        headers.setLocation(UriComponentsBuilder.newInstance().path("/api/visits/{id}")
                .buildAndExpand(visit.getId()).toUri());
        return new ResponseEntity<>(visitDto, headers, HttpStatus.CREATED);
    }

    private VisitDetailsDto toDetailsDto(Visit visit) {
        VisitDto base = visitMapper.toVisitDto(visit);
        VisitDetailsDto details = new VisitDetailsDto(base);
        if (visit.getStatus() != null) {
            details.setStatus(visit.getStatus().name());
        }
        details.setVetId(visit.getVetId());

        Optional<PetView> petOpt = petsFacade.findById(visit.getPetId());
        petOpt.ifPresent(pet -> {
            details.setPet(pet);

            if (pet.ownerId() != null) {
                ownersFacade.findById(pet.ownerId())
                        .ifPresent(details::setOwner);
            }

            if (pet.typeId() != null) {
                petTypesFacade.findById(pet.typeId())
                        .ifPresent(details::setPetType);
            }
        });

        return details;
    }

    public ResponseEntity<String> fallbackMethod(Throwable t) {
        return new ResponseEntity<>("Service temporarily unavailable. Please try again later.",
                HttpStatus.SERVICE_UNAVAILABLE);
    }
}
