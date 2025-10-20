package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityVerificationTests {

    @Test
    void verifiesModularity() {
        var modules = ApplicationModules.of(PetClinicApplication.class);

        modules.verify();
        new Documenter(modules).writeDocumentation();
        // new Documenter(modules).writeModulesAsPlantUml();   
        // new Documenter(modules).writeIndividualModulesAsPlantUml();
        // new Documenter(modules).writeModuleCanvases();
    }
}
