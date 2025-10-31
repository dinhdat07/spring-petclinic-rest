package org.springframework.samples.petclinic.owners.web.dto;

import java.time.LocalDate;

/**
 * Lightweight visit view exposed alongside pet details.
 */
public class VisitDto {

    private Integer id;

    private Integer petId;

    private LocalDate date;

    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPetId() {
        return petId;
    }

    public void setPetId(Integer petId) {
        this.petId = petId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

