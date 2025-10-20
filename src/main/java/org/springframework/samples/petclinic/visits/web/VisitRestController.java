package org.springframework.samples.petclinic.visits.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.rest.api.VisitsApi;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.rest.dto.VisitFieldsDto;
import org.springframework.samples.petclinic.visits.app.VisitService;
import org.springframework.samples.petclinic.visits.domain.Visit;
import org.springframework.samples.petclinic.visits.mapper.VisitMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api")
@RequiredArgsConstructor
public class VisitRestController implements VisitsApi {

    private final VisitService visitService;

    private final VisitMapper visitMapper;

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<List<VisitDto>> listVisits() {
        List<VisitDto> visits = new ArrayList<>(
            visitService.findAll().stream().map(visitMapper::toDto).toList()
        );
        if (visits.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(visits, HttpStatus.OK);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Override
    public ResponseEntity<VisitDto> getVisit(Integer visitId) {
        return visitService.findById(visitId)
            .map(visitMapper::toDto)
            .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
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
        VisitDto responseBody = visitMapper.toDto(visit);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(
            UriComponentsBuilder.newInstance()
                .path("/api/visits/{id}")
                .buildAndExpand(responseBody.getId())
                .toUri()
        );
        return new ResponseEntity<>(responseBody, headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @Transactional
    @Override
    public ResponseEntity<VisitDto> updateVisit(Integer visitId, VisitFieldsDto visitDto) {
        Optional<VisitDto> updated = visitService.findById(visitId).map(existing -> {
            if (visitDto.getDate() != null) {
                existing.setDate(visitDto.getDate());
            }
            existing.setDescription(visitDto.getDescription());
            visitService.save(existing);
            return visitMapper.toDto(existing);
        });

        return updated
            .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
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
}
