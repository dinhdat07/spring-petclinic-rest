package org.springframework.samples.petclinic.vets.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.samples.petclinic.catalog.api.SpecialtiesFacade;
import org.springframework.samples.petclinic.catalog.api.SpecialtyView;
import org.springframework.samples.petclinic.vets.app.VetService;
import org.springframework.samples.petclinic.vets.domain.Vet;
import org.springframework.samples.petclinic.vets.mapper.VetMapperImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(controllers = VetRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(VetMapperImpl.class)
class VetRestControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VetService vetService;

    @MockitoBean
    private SpecialtiesFacade specialtiesFacade;

    @Test
    void listVetsReturnsNotFoundWhenEmpty() throws Exception {
        given(vetService.findAll()).willReturn(List.of());

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isNotFound());
    }

    @Test
    void listVetsReturnsData() throws Exception {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialtyIds(Set.of(10));

        given(vetService.findAll()).willReturn(List.of(vet));
        given(specialtiesFacade.findByIds(Set.of(10))).willReturn(List.of(new SpecialtyView(10, "radiology")));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk());
    }
}
