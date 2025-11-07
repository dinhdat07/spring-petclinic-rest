package org.springframework.samples.petclinic.appointments.app;

import java.util.List;
import java.util.Optional;

import org.springframework.samples.petclinic.appointments.domain.Appointment;
import org.springframework.samples.petclinic.appointments.infra.jpa.AppointmentJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentJpaRepository appointmentRepository;

    @Override
    @Transactional
    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    @Override
    public List<Appointment> findByOwnerId(Integer ownerId) {
        return appointmentRepository.findByOwnerIdOrderByStartTimeDesc(ownerId);
    }

    @Override
    public List<Appointment> findByVetId(Integer vetId) {
        return appointmentRepository.findByVetIdOrderByStartTimeDesc(vetId);
    }

    @Override
    public Optional<Appointment> findById(Integer appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    @Override
    public Optional<Appointment> findByIdAndOwnerId(Integer appointmentId, Integer ownerId) {
        return appointmentRepository.findByIdAndOwnerId(appointmentId, ownerId);
    }

    @Override
    public Optional<Appointment> findByIdAndVetId(Integer appointmentId, Integer vetId) {
        return appointmentRepository.findByIdAndVetId(appointmentId, vetId);
    }

    @Override
    @Transactional
    public void delete(Appointment appointment) {
        appointmentRepository.delete(appointment);
    }
}
