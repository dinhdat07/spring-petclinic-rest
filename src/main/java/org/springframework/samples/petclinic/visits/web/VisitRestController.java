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

package org.springframework.samples.petclinic.visits.web;

import java.net.URI;
import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.samples.petclinic.platform.web.WebPageables;
import org.springframework.samples.petclinic.rest.api.VisitsApi;
import org.springframework.samples.petclinic.rest.dto.PageVisitDto;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.rest.dto.VisitFieldsDto;

import org.springframework.samples.petclinic.visits.api.VisitCommand;
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.samples.petclinic.visits.api.VisitsFacade;
import org.springframework.samples.petclinic.visits.mapper.VisitMapper;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api")
@RequiredArgsConstructor
public class VisitRestController implements VisitsApi {

  private final VisitsFacade visits;     
  private final VisitMapper visitMapper; 

  @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
  @Override
  public ResponseEntity<PageVisitDto> listVisits(
      @Min(0) @Valid Integer page,
      @Min(1) @Max(100) @Valid Integer size,
      @Valid @Nullable List<String> sort,
      @Valid @Nullable Integer petId
  ) {
    Pageable pageable = WebPageables.pageable(page, size, sort, Sort.by(Sort.Order.desc("date")));
    Page<VisitView> pageView = (petId == null)
        ? visits.findAll(pageable)
        : visits.findByPetId(petId, pageable);

    Page<VisitDto> pageDto = pageView.map(visitMapper::toDto);
    return ResponseEntity.ok(PageDtos.visit(pageDto));
  }

  @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
  @Override
  public ResponseEntity<VisitDto> getVisit(Integer visitId) {
    return visits.findById(visitId)
        .map(visitMapper::toDto)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
  @Override
  public ResponseEntity<VisitDto> addVisit(VisitDto visitDto) {
    VisitView created = visits.create(new VisitCommand.Create(
        visitDto.getPetId(),
        visitDto.getDescription(),
        visitDto.getDate() 
    ));

    URI location = URI.create("/api/visits/" + created.id());
    return ResponseEntity.created(location).body(visitMapper.toDto(created));
  }

  @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
  @Override
  public ResponseEntity<VisitDto> updateVisit(Integer visitId, VisitFieldsDto visitDto) {
    boolean updated = visits.update(visitId, new VisitCommand.Update(
        visitDto.getDescription(),
        visitDto.getDate()
    )).isPresent();

    if (!updated) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    return ResponseEntity.noContent().build(); // 204, không trả body
  }

  @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
  @Override
  public ResponseEntity<VisitDto> deleteVisit(Integer visitId) {
    boolean deleted = visits.delete(visitId);
    if (!deleted) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    return ResponseEntity.noContent().build(); // 204
  }
}
