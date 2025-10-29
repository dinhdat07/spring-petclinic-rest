package org.springframework.samples.petclinic.iam.web;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.iam.app.UserService;
import org.springframework.samples.petclinic.iam.mapper.UserMapperImpl;
import org.springframework.samples.petclinic.iam.web.dto.RoleDto;
import org.springframework.samples.petclinic.iam.web.dto.UserDto;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(controllers = UserRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(UserMapperImpl.class)
class UserRestControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void addUserReturnsCreated() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername("jane");
        userDto.setPassword("secret");
        userDto.setEnabled(true);
        userDto.setRoles(java.util.List.of(new RoleDto().name("ROLE_ADMIN")));

        doNothing().when(userService).saveUser(org.mockito.ArgumentMatchers.any());

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
            .andExpect(status().isCreated());
    }
}
