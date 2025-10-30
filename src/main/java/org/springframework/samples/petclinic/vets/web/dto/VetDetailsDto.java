package org.springframework.samples.petclinic.vets.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.samples.petclinic.catalog.api.SpecialtyView;

/**
 * Extends {@link VetDto} with resolved specialty details.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VetDetailsDto extends VetDto {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<SpecialtyView> specialties = List.of();

    public VetDetailsDto() {
        super();
    }

    public VetDetailsDto(VetDto source) {
        this();
        if (source != null) {
            this.setFirstName(source.getFirstName());
            this.setLastName(source.getLastName());
            this.setSpecialtyIds(source.getSpecialtyIds());
            this.setId(source.getId());
        }
    }

    @JsonProperty("specialties")
    public List<SpecialtyView> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<SpecialtyView> specialties) {
        this.specialties = specialties == null ? List.of() : List.copyOf(specialties);
    }
}

