package org.springframework.samples.petclinic.appointments.app.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;
import org.springframework.samples.petclinic.appointments.api.AppointmentView;
import org.springframework.samples.petclinic.appointments.app.AppointmentMapper;
import org.springframework.samples.petclinic.appointments.app.AppointmentService;
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.appointments.events.AppointmentVisitLinkedEvent;
import org.springframework.samples.petclinic.appointments.domain.Appointment;
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.samples.petclinic.visits.api.VisitsFacade;
import org.springframework.samples.petclinic.visits.api.VisitStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AppointmentWorkflowServiceTests {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private VisitsFacade visitsFacade;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private AppointmentWorkflowService appointmentWorkflowService;

    @BeforeEach
    void setUp() {
        appointmentWorkflowService = new AppointmentWorkflowService(
            appointmentService,
            visitsFacade,
            new AppointmentMapper(),
            applicationEventPublisher
        );
    }

    @Test
    void findQueueDefaultsToPendingStatus() {
        Appointment appointment = buildAppointment(AppointmentStatus.PENDING);
        when(appointmentService.findByStatuses(List.of(AppointmentStatus.PENDING))).thenReturn(List.of(appointment));

        List<AppointmentView> result = appointmentWorkflowService.findQueue(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(AppointmentStatus.PENDING);
    }

    @Test
    void confirmTransitionsPendingAppointment() {
        Appointment appointment = buildAppointment(AppointmentStatus.PENDING);
        when(appointmentService.findById(appointment.getId())).thenReturn(Optional.of(appointment));
        when(appointmentService.save(appointment)).thenReturn(appointment);

        AppointmentView view = appointmentWorkflowService.confirm(
            appointment.getId(),
            new AppointmentConfirmationCommand("triaged", 15)
        );

        assertThat(view.status()).isEqualTo(AppointmentStatus.CONFIRMED);
        assertThat(view.vetId()).isEqualTo(15);
        assertThat(view.triageNotes()).isEqualTo("triaged");
        verify(applicationEventPublisher).publishEvent(any(AppointmentConfirmedEvent.class));
    }

    @Test
    void createVisitLinksVisitAndCompletesAppointment() {
        Appointment appointment = buildAppointment(AppointmentStatus.CONFIRMED);
        when(appointmentService.findById(appointment.getId())).thenReturn(Optional.of(appointment));
        when(appointmentService.save(appointment)).thenReturn(appointment);
        when(visitsFacade.createVisit(any())).thenReturn(
            new VisitView(55, appointment.getPetId(), LocalDate.now(), "Follow up", VisitStatus.SCHEDULED, null)
        );
        when(visitsFacade.updateVisit(anyInt(), any())).thenReturn(
            Optional.of(new VisitView(55, appointment.getPetId(), LocalDate.now(), "Follow up", VisitStatus.SCHEDULED, 9))
        );

        AppointmentView view = appointmentWorkflowService.createVisit(
            appointment.getId(),
            new AppointmentVisitCommand(LocalDate.now(), "Follow up", 9, "done")
        );

        assertThat(view.visitId()).isEqualTo(55);
        assertThat(view.status()).isEqualTo(AppointmentStatus.COMPLETED);
        assertThat(view.vetId()).isEqualTo(9);
        assertThat(view.triageNotes()).isEqualTo("done");
        verify(applicationEventPublisher).publishEvent(any(AppointmentVisitLinkedEvent.class));
    }

    @Test
    void findQueueRejectsEmptyStatusList() {
        assertThatThrownBy(() -> appointmentWorkflowService.findQueue(List.of()))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("status");
    }

    private Appointment buildAppointment(AppointmentStatus status) {
        Appointment appointment = new Appointment();
        appointment.setId(42);
        appointment.setOwnerId(6);
        appointment.setPetId(3);
        appointment.setVetId(8);
        appointment.setStartTime(LocalDateTime.now().plusDays(1));
        appointment.setStatus(status);
        appointment.setNotes("notes");
        appointment.setCreatedAt(LocalDateTime.now().minusDays(1));
        appointment.setUpdatedAt(LocalDateTime.now().minusHours(2));
        return appointment;
    }
}
