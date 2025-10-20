package org.springframework.samples.petclinic.vets.api;

import java.util.List;

public record VetView(int id, String firstName, String lastName, List<SpecialtyRef> specialties) {
  public record SpecialtyRef(int id, String name) {}
}

