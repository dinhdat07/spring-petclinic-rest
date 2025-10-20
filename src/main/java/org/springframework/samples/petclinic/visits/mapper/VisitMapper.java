package org.springframework.samples.petclinic.visits.mapper;

import org.mapstruct.*;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.rest.dto.VisitFieldsDto;
import org.springframework.samples.petclinic.visits.domain.Visit;

@Mapper
public interface VisitMapper {

    VisitDto toDto(Visit entity);

    Visit toEntity(VisitDto dto);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromFields(VisitFieldsDto dto, @MappingTarget Visit entity);
}
