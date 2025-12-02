package org.springframework.samples.petclinic.owners.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Extends {@link PetDto} with denormalised data fetched from other modules.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PetDetailsDto extends PetDto {

    private PetTypeDto type;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<VisitDto> visits = List.of();

    public PetDetailsDto() {
        super();
    }

    public PetDetailsDto(PetDto source) {
        this();
        if (source != null) {
            this.setName(source.getName());
            this.setBirthDate(source.getBirthDate());
            this.setTypeId(source.getTypeId());
            this.setId(source.getId());
            this.setOwnerId(source.getOwnerId());
        }
    }

    @JsonProperty("type")
    public PetTypeDto getType() {
        return type;
    }

    public void setType(PetTypeDto type) {
        this.type = type;
    }

    @JsonProperty("visits")
    public List<VisitDto> getVisits() {
        return visits;
    }

    public void setVisits(List<VisitDto> visits) {
        this.visits = visits == null ? List.of() : List.copyOf(visits);
    }
}
