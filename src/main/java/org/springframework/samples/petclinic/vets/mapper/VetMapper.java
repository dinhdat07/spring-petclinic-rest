package org.springframework.samples.petclinic.vets.mapper;

import java.util.Collection;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.samples.petclinic.rest.dto.VetDto;
import org.springframework.samples.petclinic.rest.dto.VetFieldsDto;
import org.springframework.samples.petclinic.vets.domain.Vet;

@Mapper(componentModel = "spring")
public interface VetMapper {

    @Mapping(target = "specialtyIds", ignore = true)
    Vet toVet(VetDto vetDto);

    @Mapping(target = "specialtyIds", ignore = true)
    Vet toVet(VetFieldsDto vetFieldsDto);

    @Mapping(target = "specialties", ignore = true)
    VetDto toVetDto(Vet vet);

    Collection<VetDto> toVetDtos(Collection<Vet> vets);
}
