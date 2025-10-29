package org.springframework.samples.petclinic.visits.mapper;

import org.mapstruct.*;
import org.springframework.samples.petclinic.visits.domain.Visit;
import org.springframework.samples.petclinic.visits.web.dto.VisitDto;
import org.springframework.samples.petclinic.visits.web.dto.VisitFieldsDto;

@Mapper
public interface VisitMapper {

    VisitDto toVisitDto(Visit entity);

    @Mapping(target = "id", ignore = true)
    Visit toVisit(VisitDto dto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromFields(VisitFieldsDto dto, @MappingTarget Visit entity);
}
