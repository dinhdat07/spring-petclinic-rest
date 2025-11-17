package org.springframework.samples.petclinic.appointments.infra.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;
import org.springframework.samples.petclinic.appointments.domain.Appointment;

public interface AppointmentJpaRepository extends JpaRepository<Appointment, Integer> {

    List<Appointment> findByOwnerIdOrderByStartTimeDesc(Integer ownerId);

    List<Appointment> findByVetIdOrderByStartTimeDesc(Integer vetId);

    List<Appointment> findByStatusInOrderByStartTimeAsc(List<AppointmentStatus> statuses);

    Optional<Appointment> findByIdAndOwnerId(Integer id, Integer ownerId);

    Optional<Appointment> findByIdAndVetId(Integer id, Integer vetId);
}
