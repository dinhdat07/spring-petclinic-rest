package org.springframework.samples.petclinic.catalog.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.samples.petclinic.catalog.app.pettype.PetTypeService;
import org.springframework.samples.petclinic.catalog.domain.PetType;
import org.springframework.samples.petclinic.catalog.mapper.PetTypeMapperImpl;
import org.springframework.samples.petclinic.platform.props.Roles;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PetTypeRestController.class)
@Import({PetTypeMapperImpl.class, Roles.class})
class PetTypeRestControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PetTypeServiceStub petTypeServiceStub;

    @TestConfiguration
    static class StubConfig {
        @Bean
        PetTypeServiceStub petTypeServiceStub() {
            return new PetTypeServiceStub();
        }

        @Bean
        PetTypeService petTypeService(PetTypeServiceStub stub) {
            return stub;
        }
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void listPetTypesReturnsNotFoundWhenEmpty() throws Exception {
        petTypeServiceStub.setPetTypes(List.of());

        mockMvc.perform(get("/api/pettypes"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void listPetTypesReturnsData() throws Exception {
        PetType petType = new PetType();
        petType.setId(1);
        petType.setName("dog");

        petTypeServiceStub.setPetTypes(List.of(petType));

        mockMvc.perform(get("/api/pettypes"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void getPetTypeReturnsNotFoundWhenMissing() throws Exception {
        petTypeServiceStub.setFindById(Optional.empty());

        mockMvc.perform(get("/api/pettypes/99"))
            .andExpect(status().isNotFound());
    }

    static class PetTypeServiceStub implements PetTypeService {

        private Collection<PetType> petTypes = List.of();
        private Optional<PetType> findByIdOverride = null;

        void setPetTypes(Collection<PetType> petTypes) {
            this.petTypes = petTypes;
            this.findByIdOverride = null;
        }

        void setFindById(Optional<PetType> petType) {
            this.findByIdOverride = petType;
        }

        @Override
        public Optional<PetType> findById(int id) {
            if (findByIdOverride != null) {
                return findByIdOverride;
            }
            return petTypes.stream()
                .filter(type -> type.getId() != null && type.getId() == id)
                .findFirst();
        }

        @Override
        public Collection<PetType> findAll() {
            return petTypes;
        }

        @Override
        public void save(PetType petType) {
            // no-op for stub
        }

        @Override
        public void delete(PetType petType) {
            // no-op for stub
        }
    }
}
