package org.springframework.samples.petclinic.owners.mapper;

import java.util.Collection;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.samples.petclinic.owners.domain.Pet;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.dto.PetFieldsDto;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
public interface PetMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(target = "visits", ignore = true)
    @Mapping(target = "type", ignore = true)
    PetDto toPetDto(Pet pet);

    Collection<PetDto> toPetsDto(Collection<Pet> pets);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "typeId", source = "type.id")
    Pet toPet(PetDto petDto);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "typeId", source = "type.id")
    Pet toPet(PetFieldsDto petFieldsDto);
}
