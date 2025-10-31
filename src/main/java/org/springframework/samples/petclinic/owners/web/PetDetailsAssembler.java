package org.springframework.samples.petclinic.owners.web;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.samples.petclinic.catalog.api.PetTypeView;
import org.springframework.samples.petclinic.catalog.api.PetTypesFacade;
import org.springframework.samples.petclinic.owners.domain.Pet;
import org.springframework.samples.petclinic.owners.mapper.PetMapper;
import org.springframework.samples.petclinic.owners.web.dto.PetDto;
import org.springframework.samples.petclinic.owners.web.dto.PetDetailsDto;
import org.springframework.samples.petclinic.owners.web.dto.PetTypeDto;
import org.springframework.samples.petclinic.owners.web.dto.VisitDto;
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.samples.petclinic.visits.api.VisitsFacade;
import org.springframework.stereotype.Component;

@Component
public class PetDetailsAssembler {

    private final PetMapper petMapper;
    private final PetTypesFacade petTypesFacade;
    private final VisitsFacade visitsFacade;

    public PetDetailsAssembler(
        PetMapper petMapper,
        PetTypesFacade petTypesFacade,
        VisitsFacade visitsFacade
    ) {
        this.petMapper = petMapper;
        this.petTypesFacade = petTypesFacade;
        this.visitsFacade = visitsFacade;
    }

    public PetDto toDetailedDto(Pet pet) {
        PetDto base = petMapper.toPetDto(pet);
        PetDetailsDto detailed = new PetDetailsDto(base);

        if (pet != null && pet.getId() != null) {
            List<VisitDto> visits = visitsFacade.findByPetId(pet.getId()).stream()
                .filter(Objects::nonNull)
                .map(this::toVisitDto)
                .collect(Collectors.toList());
            detailed.setVisits(visits);
        }

        if (base.getTypeId() != null) {
            petTypesFacade.findById(base.getTypeId())
                .map(this::toPetTypeDto)
                .ifPresent(detailed::setType);
        }

        return detailed;
    }

    public List<PetDto> toDetailedDtos(Collection<Pet> pets) {
        return pets == null ? List.of()
            : pets.stream()
                .filter(Objects::nonNull)
                .map(this::toDetailedDto)
                .collect(Collectors.toList());
    }

    private VisitDto toVisitDto(VisitView view) {
        VisitDto dto = new VisitDto();
        dto.setId(view.id());
        dto.setPetId(view.petId());
        dto.setDate(view.date());
        dto.setDescription(view.description());
        return dto;
    }

    private PetTypeDto toPetTypeDto(PetTypeView view) {
        PetTypeDto dto = new PetTypeDto();
        dto.setId(view.id());
        dto.setName(view.name());
        return dto;
    }
}

