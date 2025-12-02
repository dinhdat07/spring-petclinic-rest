package org.springframework.samples.petclinic.vets.mapper;

import java.util.Collection;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.springframework.samples.petclinic.vets.domain.Vet;
import org.springframework.samples.petclinic.vets.web.dto.VetDto;
import org.springframework.samples.petclinic.vets.web.dto.VetFieldsDto;

@Mapper(componentModel = "spring")
public interface VetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specialtyIds", ignore = true)
    Vet toVet(VetDto vetDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specialtyIds", ignore = true)
    Vet toVet(VetFieldsDto vetFieldsDto);

    @Mapping(target = "specialtyIds", ignore = true)
    @Mapping(target = "specialties", ignore = true)
    VetDto toVetDto(Vet vet);

    Collection<VetDto> toVetDtos(Collection<Vet> vets);
}
