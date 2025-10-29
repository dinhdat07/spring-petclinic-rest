package org.springframework.samples.petclinic.visits.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.samples.petclinic.visits.app.VisitService;
import org.springframework.samples.petclinic.visits.domain.Visit;
import org.springframework.samples.petclinic.visits.mapper.VisitMapper;
import org.springframework.samples.petclinic.platform.props.Roles;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(controllers = VisitRestController.class)
@Import(Roles.class)
class VisitRestControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VisitService visitService;

    @MockitoBean
    private VisitMapper visitMapper;

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void listVisitsReturnsNotFoundWhenEmpty() throws Exception {
        given(visitService.findAll()).willReturn(List.of());

        mockMvc.perform(get("/api/visits"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void listVisitsReturnsData() throws Exception {
        given(visitService.findAll()).willReturn(List.of(new Visit(1, null, "Checkup", 10)));

        mockMvc.perform(get("/api/visits"))
            .andExpect(status().isOk());
    }
}
