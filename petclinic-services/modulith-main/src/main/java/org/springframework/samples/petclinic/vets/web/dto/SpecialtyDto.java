package org.springframework.samples.petclinic.vets.web.dto;

/**
 * Minimal representation of a vet specialty exposed in responses.
 */
public class SpecialtyDto {

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

