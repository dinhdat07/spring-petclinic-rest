package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityVerificationTests {

    @Test
    void verifiesModularity() {
        ApplicationModules.of(PetClinicApplication.class).verify();
    }
}
