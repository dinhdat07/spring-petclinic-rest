package org.springframework.samples.petclinic.owners.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.samples.petclinic.catalog.api.PetTypeView;
import org.springframework.samples.petclinic.catalog.api.PetTypesFacade;
import org.springframework.samples.petclinic.owners.app.pet.PetService;
import org.springframework.samples.petclinic.owners.domain.Owner;
import org.springframework.samples.petclinic.owners.domain.Pet;
import org.springframework.samples.petclinic.owners.mapper.PetMapperImpl;
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.samples.petclinic.visits.api.VisitsFacade;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PetRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(PetMapperImpl.class)
class PetRestControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService petService;

    @MockBean
    private PetTypesFacade petTypesFacade;

    @MockBean
    private VisitsFacade visitsFacade;

    @Test
    void getPetReturnsNotFoundWhenMissing() throws Exception {
        given(petService.findById(1)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/pets/1"))
            .andExpect(status().isNotFound());
    }

    @Test
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
        given(visitsFacade.findByPetId(1)).willReturn(List.of(new VisitView(100, 1, null, "Checkup")));

        mockMvc.perform(get("/api/pets/1"))
            .andExpect(status().isOk());
    }
}
