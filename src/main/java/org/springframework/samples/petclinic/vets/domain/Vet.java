package org.springframework.samples.petclinic.vets.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import org.springframework.samples.petclinic.common.Identifiable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Simple JavaBean domain object representing a veterinarian.
 */
@Entity
@Table(name = "vets")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Vet implements Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false)
    @NotBlank
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank
    private String lastName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "vet_specialties", joinColumns = @JoinColumn(name = "vet_id"))
    @Column(name = "specialty_id")
    private Set<Integer> specialtyIds;

    public Set<Integer> getSpecialtyIds() {
        if (this.specialtyIds == null) {
            this.specialtyIds = new HashSet<>();
        }
        return Collections.unmodifiableSet(this.specialtyIds);
    }

    public void setSpecialtyIds(Set<Integer> specialtyIds) {
        this.specialtyIds = new HashSet<>(specialtyIds);
    }

    public void addSpecialtyId(Integer specialtyId) {
        if (this.specialtyIds == null) {
            this.specialtyIds = new HashSet<>();
        }
        this.specialtyIds.add(specialtyId);
    }

    public void clearSpecialtyIds() {
        if (this.specialtyIds != null) {
            this.specialtyIds.clear();
        }
    }
}
