package org.springframework.samples.petclinic.owners.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.samples.petclinic.catalog.api.PetTypeView;
import org.springframework.samples.petclinic.visits.api.VisitView;

/**
 * Extends {@link PetDto} with denormalised data fetched from other modules.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PetDetailsDto extends PetDto {

    private PetTypeView type;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<VisitView> visits = List.of();

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
    public PetTypeView getType() {
        return type;
    }

    public void setType(PetTypeView type) {
        this.type = type;
    }

    @JsonProperty("visits")
    public List<VisitView> getVisits() {
        return visits;
    }

    public void setVisits(List<VisitView> visits) {
        this.visits = visits == null ? List.of() : List.copyOf(visits);
    }
}

