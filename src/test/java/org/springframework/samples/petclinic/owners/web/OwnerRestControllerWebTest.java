package org.springframework.samples.petclinic.owners.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.samples.petclinic.owners.mapper.OwnerMapperImpl;
import org.springframework.samples.petclinic.owners.mapper.PetMapperImpl;
import org.springframework.samples.petclinic.platform.props.Roles;
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.samples.petclinic.visits.api.VisitsFacade;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(controllers = OwnerRestController.class)
@Import({OwnerMapperImpl.class, PetMapperImpl.class, Roles.class})
class OwnerRestControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OwnerService ownerService;

    @MockitoBean
    private PetService petService;

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
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void getOwnersPetReturnsNotFoundForMissingOwner() throws Exception {
        given(ownerService.findById(1)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/owners/1/pets/1"))
            .andExpect(status().isNotFound());
    }
}
