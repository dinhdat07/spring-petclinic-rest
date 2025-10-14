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
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.samples.petclinic.visits.api.VisitsFacade;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = VisitRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class VisitRestControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VisitsFacade visitsFacade;

    @Test
    void listVisitsReturnsNotFoundWhenEmpty() throws Exception {
        given(visitsFacade.findAll()).willReturn(List.of());

        mockMvc.perform(get("/api/visits"))
            .andExpect(status().isNotFound());
    }

    @Test
    void listVisitsReturnsData() throws Exception {
        given(visitsFacade.findAll()).willReturn(List.of(new VisitView(1, 10, null, "Checkup")));

        mockMvc.perform(get("/api/visits"))
            .andExpect(status().isOk());
    }
}
