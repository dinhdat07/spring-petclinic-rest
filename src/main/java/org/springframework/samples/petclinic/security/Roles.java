package org.springframework.samples.petclinic.security;

import org.springframework.stereotype.Component;

@Component
public class Roles {

    public final String OWNER_ADMIN = "OWNER_ADMIN";
    public final String VET_ADMIN = "VET_ADMIN";
    public final String ADMIN = "ADMIN";
}
