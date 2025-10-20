package org.springframework.samples.petclinic.visits.mapper;

import org.mapstruct.Mapper;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.samples.petclinic.visits.domain.Visit;

/**
 * Map Visit & VisitDto using mapstruct
 */
@Mapper(componentModel = "spring")
public interface VisitMapper {
    Visit toVisit(VisitDto visitDto);

    VisitDto toDto(VisitView v);

    VisitDto toDto(Visit visit);

    VisitView toView(Visit v);

}