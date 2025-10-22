package org.springframework.samples.petclinic.iam.api;

import java.util.Set;

/**
 * Read model exposed for authentication needs.
 */
public record UserAuthView(
    String username,
    String password,
    boolean enabled,
    Set<String> roles
) {}

