package org.springframework.samples.petclinic.scheduling.infra.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.scheduling.domain.AppointmentSlotAllocation;

public interface AppointmentSlotAllocationRepository extends JpaRepository<AppointmentSlotAllocation, Long> {

    Optional<AppointmentSlotAllocation> findByAppointmentId(Integer appointmentId);
}
