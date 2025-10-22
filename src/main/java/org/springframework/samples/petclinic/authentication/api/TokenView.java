package org.springframework.samples.petclinic.authentication.api;

import org.springframework.modulith.NamedInterface;

@NamedInterface
public record TokenView(String accessToken) {}

