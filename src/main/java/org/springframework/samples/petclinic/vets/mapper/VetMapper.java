// src/main/java/org/springframework/samples/petclinic/vets/mapper/VetMapper.java
package org.springframework.samples.petclinic.vets.mapper;

import org.mapstruct.*;
import org.springframework.samples.petclinic.vets.api.VetView;
import org.springframework.samples.petclinic.rest.dto.VetDto;
import org.springframework.samples.petclinic.rest.dto.SpecialtyDto;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface VetMapper {
    VetDto toDto(VetView view);
    
    SpecialtyDto toDto(VetView.SpecialtyRef ref);
    
    default Set<Integer> toSpecialtyIds(VetDto dto) {
        if (dto.getSpecialties() == null) return Set.of();
        return dto.getSpecialties().stream().map(SpecialtyDto::getId).collect(Collectors.toSet());
    }
}
