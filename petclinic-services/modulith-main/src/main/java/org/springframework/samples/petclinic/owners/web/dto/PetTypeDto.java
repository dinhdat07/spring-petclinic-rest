package org.springframework.samples.petclinic.owners.web.dto;

/**
 * Minimal representation of a pet type used in enriched responses.
 */
public class PetTypeDto {

    private Integer id;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

