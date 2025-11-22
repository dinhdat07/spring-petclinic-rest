package org.springframework.samples.petclinic.scheduling.infra.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.scheduling.domain.Slot;

public interface SlotRepository extends JpaRepository<Slot, Long> {

    Optional<Slot> findByVetIdAndStartTime(Integer vetId, LocalDateTime startTime);

    List<Slot> findByVetIdAndStartTimeBetweenOrderByStartTime(Integer vetId, LocalDateTime start, LocalDateTime end);

    List<Slot> findByVetId(Integer vetId);
}
