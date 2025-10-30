package org.springframework.samples.petclinic.visits.web;

import org.springframework.samples.petclinic.catalog.api.PetTypeView;
import org.springframework.samples.petclinic.owners.api.OwnerView;
import org.springframework.samples.petclinic.owners.api.PetView;
import org.springframework.samples.petclinic.visits.web.dto.VisitDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Extends {@link VisitDto} with resolved pet details.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisitDetailsDto extends VisitDto {

    private PetView pet;

    private OwnerView owner;

    private PetTypeView petType;
    
    public VisitDetailsDto() {
        super();
    }

    public VisitDetailsDto(VisitDto source) {
        this();
        if (source != null) {
            this.setId(source.getId());
            this.setDescription(source.getDescription());
            this.setPetId(getPetId());
            this.setDate(source.getDate());
        }   
    }
    @JsonProperty("pet")
    public PetView getPet() {
        return pet;
    }

    public void setPet(PetView pet) {
        this.pet = pet;
    }


    @JsonProperty("owner")
    public OwnerView getOwner() {
        return owner;
    }

    public void setOwner(OwnerView owner) {
        this.owner = owner;
    }


    @JsonProperty("type")
    public PetTypeView getPetType() {
        return petType;
    }

    public void setPetType(PetTypeView petType) {
        this.petType = petType;
    }





}
