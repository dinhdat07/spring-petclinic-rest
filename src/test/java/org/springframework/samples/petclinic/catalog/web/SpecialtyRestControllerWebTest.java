package org.springframework.samples.petclinic.catalog.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.samples.petclinic.catalog.app.specialty.SpecialtyService;
import org.springframework.samples.petclinic.catalog.domain.Specialty;
import org.springframework.samples.petclinic.catalog.mapper.SpecialtyMapperImpl;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = SpecialtyRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(SpecialtyMapperImpl.class)
class SpecialtyRestControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpecialtyServiceStub specialtyServiceStub;

    @TestConfiguration
    static class StubConfig {
        @Bean
        SpecialtyServiceStub specialtyServiceStub() {
            return new SpecialtyServiceStub();
        }

        @Bean
        SpecialtyService specialtyService(SpecialtyServiceStub stub) {
            return stub;
        }
    }

    @Test
    void listSpecialtiesReturnsNotFoundWhenEmpty() throws Exception {
        specialtyServiceStub.setSpecialties(List.of());

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isNotFound());
    }

    @Test
    void listSpecialtiesReturnsData() throws Exception {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");

        specialtyServiceStub.setSpecialties(List.of(specialty));

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk());
    }

    @Test
    void getSpecialtyReturnsNotFoundWhenMissing() throws Exception {
        specialtyServiceStub.setFindById(Optional.empty());

        mockMvc.perform(get("/api/specialties/42"))
            .andExpect(status().isNotFound());
    }

    static class SpecialtyServiceStub implements SpecialtyService {

        private Collection<Specialty> specialties = List.of();
        private Optional<Specialty> findByIdOverride = null;

        void setSpecialties(Collection<Specialty> specialties) {
            this.specialties = specialties;
            this.findByIdOverride = null;
        }

        void setFindById(Optional<Specialty> specialty) {
            this.findByIdOverride = specialty;
        }

        @Override
        public Optional<Specialty> findById(int id) {
            if (findByIdOverride != null) {
                return findByIdOverride;
            }
            return specialties.stream()
                .filter(spec -> spec.getId() != null && spec.getId() == id)
                .findFirst();
        }

        @Override
        public Collection<Specialty> findAll() {
            return specialties;
        }

        @Override
        public void save(Specialty specialty) {
            // no-op
        }

        @Override
        public void delete(Specialty specialty) {
            // no-op
        }

        @Override
        public List<Specialty> findByNameIn(Set<String> names) {
            return specialties.stream()
                .filter(spec -> spec.getName() != null && names.contains(spec.getName()))
                .toList();
        }
    }
}
