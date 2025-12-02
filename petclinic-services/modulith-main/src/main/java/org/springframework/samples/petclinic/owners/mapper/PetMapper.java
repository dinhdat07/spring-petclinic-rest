package org.springframework.samples.petclinic.owners.mapper;

import java.util.Collection;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.samples.petclinic.owners.domain.Pet;
import org.springframework.samples.petclinic.owners.web.dto.PetDto;
import org.springframework.samples.petclinic.owners.web.dto.PetFieldsDto;

@Mapper(componentModel = "spring")
public interface PetMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(target = "typeId", source = "typeId")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "visits", ignore = true)
    PetDto toPetDto(Pet pet);

    Collection<PetDto> toPetsDto(Collection<Pet> pets);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "typeId", source = "typeId")
    Pet toPet(PetDto petDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "typeId", source = "typeId")
    Pet toPet(PetFieldsDto petFieldsDto);
}
