package org.springframework.samples.petclinic.appointments.app;

import java.util.List;
import java.util.Optional;

import org.springframework.samples.petclinic.appointments.domain.Appointment;

public interface AppointmentService {

    Appointment save(Appointment appointment);

    List<Appointment> findAll();

    List<Appointment> findByOwnerId(Integer ownerId);

    List<Appointment> findByVetId(Integer vetId);

    Optional<Appointment> findById(Integer appointmentId);

    Optional<Appointment> findByIdAndOwnerId(Integer appointmentId, Integer ownerId);

    Optional<Appointment> findByIdAndVetId(Integer appointmentId, Integer vetId);

    void delete(Appointment appointment);
}
