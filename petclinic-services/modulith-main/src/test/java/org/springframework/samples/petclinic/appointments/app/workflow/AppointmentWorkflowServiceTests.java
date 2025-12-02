package org.springframework.samples.petclinic.appointments.app.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
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
import org.springframework.samples.petclinic.appointments.api.VetContactPort;
import org.springframework.samples.petclinic.appointments.app.AppointmentMapper;
import org.springframework.samples.petclinic.appointments.app.AppointmentService;
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.appointments.events.AppointmentVisitLinkedEvent;
import org.springframework.samples.petclinic.appointments.domain.Appointment;
import org.springframework.samples.petclinic.owners.api.OwnerView;
import org.springframework.samples.petclinic.owners.api.OwnersFacade;
import org.springframework.samples.petclinic.visits.api.VisitStatus;
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.samples.petclinic.visits.api.VisitsFacade;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AppointmentWorkflowServiceTests {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private VisitsFacade visitsFacade;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private OwnersFacade ownersFacade;

    @Mock
    private VetContactPort vetContactPort;

    private AppointmentWorkflowService appointmentWorkflowService;

    @BeforeEach
    void setUp() {
        appointmentWorkflowService = new AppointmentWorkflowService(
            appointmentService,
            visitsFacade,
            new AppointmentMapper(),
            applicationEventPublisher,
            ownersFacade,
            vetContactPort
        );
        lenient().when(ownersFacade.findById(anyInt())).thenReturn(Optional.of(new OwnerView(6, "Owner", "Franklin", "owner@example.com")));
        lenient().when(vetContactPort.findContact(anyInt())).thenReturn(Optional.of(new VetContactPort.VetContact("James Carter", "vet@example.com")));
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

    @Test
    void confirmRejectsWhenOwnerContactMissing() {
        Appointment appointment = buildAppointment(AppointmentStatus.PENDING);
        when(appointmentService.findById(appointment.getId())).thenReturn(Optional.of(appointment));
        when(appointmentService.save(appointment)).thenReturn(appointment);
        when(ownersFacade.findById(appointment.getOwnerId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentWorkflowService.confirm(
            appointment.getId(),
            new AppointmentConfirmationCommand("triaged", 15)
        ))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("owner");
    }

    @Test
    void confirmRejectsWhenVetMissing() {
        Appointment appointment = buildAppointment(AppointmentStatus.PENDING);
        appointment.setVetId(null);
        when(appointmentService.findById(appointment.getId())).thenReturn(Optional.of(appointment));
        when(appointmentService.save(appointment)).thenReturn(appointment);

        assertThatThrownBy(() -> appointmentWorkflowService.confirm(
            appointment.getId(),
            new AppointmentConfirmationCommand("triaged", null)
        ))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("vet");
    }

    @Test
    void createVisitRejectsWhenVetMissing() {
        Appointment appointment = buildAppointment(AppointmentStatus.CONFIRMED);
        appointment.setVetId(null);
        when(appointmentService.findById(appointment.getId())).thenReturn(Optional.of(appointment));
        when(appointmentService.save(appointment)).thenReturn(appointment);
        when(visitsFacade.createVisit(any())).thenReturn(
            new VisitView(55, appointment.getPetId(), LocalDate.now(), "Follow up", VisitStatus.SCHEDULED, null)
        );

        assertThatThrownBy(() -> appointmentWorkflowService.createVisit(
            appointment.getId(),
            new AppointmentVisitCommand(LocalDate.now(), "Follow up", null, null)
        ))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("vet");
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
