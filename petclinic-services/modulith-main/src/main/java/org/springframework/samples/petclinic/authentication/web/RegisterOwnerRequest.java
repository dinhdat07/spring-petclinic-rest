package org.springframework.samples.petclinic.authentication.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterOwnerRequest(
    @NotBlank @Size(min = 4, max = 20) String username,
    @NotBlank @Size(min = 6, max = 60) String password,
    @NotBlank @Size(max = 30) String firstName,
    @NotBlank @Size(max = 30) String lastName,
    @NotBlank @Size(max = 255) String address,
    @NotBlank @Size(max = 80) String city,
    @NotBlank @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits") String telephone
) {
}

