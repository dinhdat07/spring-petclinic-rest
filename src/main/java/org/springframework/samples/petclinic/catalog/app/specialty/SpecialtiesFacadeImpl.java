package org.springframework.samples.petclinic.catalog.app.specialty;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

import org.springframework.samples.petclinic.catalog.api.SpecialtiesFacade;
import org.springframework.samples.petclinic.catalog.api.SpecialtyView;
import org.springframework.samples.petclinic.catalog.domain.Specialty;
import org.springframework.samples.petclinic.catalog.infra.jpa.SpecialtyJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SpecialtiesFacadeImpl implements SpecialtiesFacade {

    private final SpecialtyJpaRepository specialtyRepository;

    public SpecialtiesFacadeImpl(SpecialtyJpaRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    public Optional<SpecialtyView> findById(int id) {
        return specialtyRepository.findById(id).map(this::toView);
    }

    @Override
    public Collection<SpecialtyView> findAll() {
        return specialtyRepository.findAll().stream()
            .map(this::toView)
            .toList();
    }

    @Override
    public List<SpecialtyView> findByNames(Set<String> names) {
        if (names == null || names.isEmpty()) {
            return List.of();
        }
        return specialtyRepository.findByNameIn(names).stream()
            .map(this::toView)
            .toList();
    }

    @Override
    public List<SpecialtyView> findByIds(Set<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return StreamSupport.stream(specialtyRepository.findAllById(ids).spliterator(), false)
            .map(this::toView)
            .toList();
    }

    private SpecialtyView toView(Specialty specialty) {
        return new SpecialtyView(specialty.getId(), specialty.getName());
    }
}
