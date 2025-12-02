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
import org.springframework.samples.petclinic.owners.app.pet.PetService;
import org.springframework.samples.petclinic.owners.domain.Owner;
import org.springframework.samples.petclinic.owners.domain.Pet;
import org.springframework.samples.petclinic.MapStructTestConfiguration;
import org.springframework.samples.petclinic.platform.props.Roles;
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.samples.petclinic.visits.api.VisitStatus;
import org.springframework.samples.petclinic.visits.api.VisitsFacade;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.samples.petclinic.owners.web.PetDetailsAssembler;

@WebMvcTest(controllers = PetRestController.class)
@Import({MapStructTestConfiguration.class, PetDetailsAssembler.class, Roles.class})
class PetRestControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PetService petService;

    @MockitoBean
    private OwnerService ownerService;

    @MockitoBean
    private PetTypesFacade petTypesFacade;

    @MockitoBean
    private VisitsFacade visitsFacade;

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void getPetReturnsNotFoundWhenMissing() throws Exception {
        given(petService.findById(1)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/pets/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void getPetReturnsData() throws Exception {
        Owner owner = new Owner();
        owner.setId(5);

        Pet pet = new Pet();
        pet.setId(1);
        pet.setOwner(owner);
        pet.setName("Rosy");
        pet.setTypeId(2);

        given(petService.findById(1)).willReturn(Optional.of(pet));
        given(petTypesFacade.findById(2)).willReturn(Optional.of(new PetTypeView(2, "dog")));
        given(visitsFacade.findByPetId(1)).willReturn(List.of(new VisitView(100, 1, null, "Checkup", VisitStatus.SCHEDULED, null)));

        mockMvc.perform(get("/api/pets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.type.name").value("dog"))
            .andExpect(jsonPath("$.visits[0].description").value("Checkup"));
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void getOwnersPetReturnsNotFoundWhenOwnerMissing() throws Exception {
        given(ownerService.findById(1)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/owners/1/pets/1"))
            .andExpect(status().isNotFound());
    }
}
