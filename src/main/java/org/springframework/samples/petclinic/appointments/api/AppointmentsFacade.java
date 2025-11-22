package org.springframework.samples.petclinic.appointments.api;

import java.util.List;
import java.util.Optional;

public interface AppointmentsFacade {

    AppointmentView create(AppointmentCreateCommand command);

    List<AppointmentView> findAll();

    List<AppointmentView> findByOwnerId(Integer ownerId);

    List<AppointmentView> findByVetId(Integer vetId);

    Optional<AppointmentView> findById(Integer appointmentId);

    Optional<AppointmentView> findForOwner(Integer appointmentId, Integer ownerId);

    Optional<AppointmentView> findForVet(Integer appointmentId, Integer vetId);

    Optional<AppointmentView> update(Integer appointmentId, AppointmentUpdateCommand command);

    boolean delete(Integer appointmentId);
}
