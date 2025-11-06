package org.springframework.samples.petclinic.owners.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.samples.petclinic.catalog.api.PetTypeView;
import org.springframework.samples.petclinic.catalog.api.PetTypesFacade;
import org.springframework.samples.petclinic.owners.app.owner.OwnerService;
import org.springframework.samples.petclinic.owners.domain.Owner;
import org.springframework.samples.petclinic.owners.domain.Pet;
import org.springframework.samples.petclinic.MapStructTestConfiguration;
import org.springframework.samples.petclinic.platform.props.Roles;
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.samples.petclinic.visits.api.VisitsFacade;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.samples.petclinic.owners.web.PetDetailsAssembler;

@WebMvcTest(controllers = OwnerRestController.class)
@Import({MapStructTestConfiguration.class, PetDetailsAssembler.class, Roles.class})
class OwnerRestControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OwnerService ownerService;

    @MockitoBean
    private VisitsFacade visitsFacade;

    @MockitoBean
    private PetTypesFacade petTypesFacade;

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void listOwnersReturnsNotFoundWhenEmpty() throws Exception {
        given(ownerService.findAll()).willReturn(List.of());

        mockMvc.perform(get("/api/owners"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void listOwnersReturnsData() throws Exception {
        Owner owner = new Owner();
        owner.setId(1);
        owner.setFirstName("George");
        owner.setLastName("Franklin");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("6085551023");

        Pet pet = new Pet();
        pet.setId(10);
        pet.setName("Rosy");
        pet.setOwner(owner);
        pet.setTypeId(2);
        owner.setPets(List.of(pet));

        given(ownerService.findAll()).willReturn(List.of(owner));
        given(visitsFacade.findByPetId(10)).willReturn(List.of(new VisitView(100, 10, null, "Checkup")));
        given(petTypesFacade.findById(2)).willReturn(Optional.of(new PetTypeView(2, "dog")));

        mockMvc.perform(get("/api/owners"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].pets[0].type.name").value("dog"))
            .andExpect(jsonPath("$[0].pets[0].visits[0].description").value("Checkup"));
    }

}
