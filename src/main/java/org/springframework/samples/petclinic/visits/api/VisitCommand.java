package org.springframework.samples.petclinic.visits.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public final class VisitCommand {
  private VisitCommand() {}

  public record Create(
      @NotNull Integer petId,
      @NotBlank @Size(max = 255) String description,
      LocalDate date // optional
  ) {}

  public record Update(
      @NotBlank @Size(max = 255) String description,
      LocalDate date // optional
  ) {}
}
