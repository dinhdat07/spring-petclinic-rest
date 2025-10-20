package org.springframework.samples.petclinic.visits.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.samples.petclinic.visits.app.VisitService;
import org.springframework.samples.petclinic.visits.domain.Visit;
import org.springframework.samples.petclinic.visits.mapper.VisitMapper;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = VisitRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class VisitRestControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VisitService visitService;

    @MockBean
    private VisitMapper visitMapper;

    @Test
    void listVisitsReturnsNotFoundWhenEmpty() throws Exception {
        given(visitService.findAll()).willReturn(List.of());

        mockMvc.perform(get("/api/visits"))
            .andExpect(status().isNotFound());
    }

    @Test
    void listVisitsReturnsData() throws Exception {
        given(visitService.findAll()).willReturn(List.of(new Visit(1, null, "Checkup", 10)));

        mockMvc.perform(get("/api/visits"))
            .andExpect(status().isOk());
    }
}
