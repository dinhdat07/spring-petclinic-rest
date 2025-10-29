package org.springframework.samples.petclinic.catalog.mapper;

import org.mapstruct.Mapper;
import org.springframework.samples.petclinic.catalog.web.dto.SpecialtyDto;
import org.springframework.samples.petclinic.catalog.domain.Specialty;

import java.util.Collection;

/**
 * Map Specialty & SpecialtyDto using mapstruct
 */
@Mapper(componentModel = "spring")
public interface SpecialtyMapper {
    Specialty toSpecialty(SpecialtyDto specialtyDto);

    SpecialtyDto toSpecialtyDto(Specialty specialty);

    Collection<SpecialtyDto> toSpecialtyDtos(Collection<Specialty> specialties);

    Collection<Specialty> toSpecialtys(Collection<SpecialtyDto> specialties);

}
