package org.springframework.samples.petclinic.vets.app.self;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;
import org.springframework.samples.petclinic.appointments.api.AppointmentView;
import org.springframework.samples.petclinic.appointments.api.AppointmentsFacade;
import org.springframework.samples.petclinic.vets.app.VetService;
import org.springframework.samples.petclinic.vets.domain.Vet;
import org.springframework.samples.petclinic.visits.api.VisitView;
import org.springframework.samples.petclinic.visits.api.VisitsFacade;
import org.springframework.samples.petclinic.visits.api.VisitStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class VetSelfServiceTests {

    @Mock
    private VetService vetService;

    @Mock
    private AppointmentsFacade appointmentsFacade;

    @Mock
    private VisitsFacade visitsFacade;

    @InjectMocks
    private VetSelfService vetSelfService;

    private Vet vet;

    @BeforeEach
    void setUp() {
        vet = new Vet();
        vet.setId(5);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setUsername("vet");
    }

    @Test
    void requireCurrentVetReturnsProfile() {
        when(vetService.findByUsername("vet")).thenReturn(Optional.of(vet));

        Vet result = vetSelfService.requireCurrentVet("vet");

        assertThat(result).isSameAs(vet);
    }

    @Test
    void confirmAppointmentUpdatesStatus() {
        LocalDateTime future = LocalDateTime.now().plusDays(2);
        AppointmentView pending = new AppointmentView(
            10,
            2,
            7,
            vet.getId(),
            future,
            AppointmentStatus.PENDING,
            "Routine check",
            future.minusDays(1),
            future.minusDays(1)
        );
        AppointmentView confirmed = new AppointmentView(
            10,
            2,
            7,
            vet.getId(),
            future,
            AppointmentStatus.CONFIRMED,
            "Routine check",
            future.minusDays(1),
            future
        );

        when(appointmentsFacade.findForVet(10, vet.getId())).thenReturn(Optional.of(pending));
        when(appointmentsFacade.update(eq(10), any())).thenReturn(Optional.of(confirmed));

        AppointmentView result = vetSelfService.confirmAppointment(vet, 10);

        assertThat(result.status()).isEqualTo(AppointmentStatus.CONFIRMED);
        verify(appointmentsFacade).update(eq(10), any());
    }

    @Test
    void confirmAppointmentThrowsIfNotAssigned() {
        when(appointmentsFacade.findForVet(10, vet.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetSelfService.confirmAppointment(vet, 10))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining(String.valueOf(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void completeVisitAssignsVetAndStatus() {
        VisitView visit = new VisitView(
            99,
            3,
            java.time.LocalDate.now(),
            "desc",
            VisitStatus.SCHEDULED,
            null
        );
        VisitView completed = new VisitView(
            99,
            3,
            visit.date(),
            visit.description(),
            VisitStatus.COMPLETED,
            vet.getId()
        );

        when(visitsFacade.findById(99)).thenReturn(Optional.of(visit));
        when(visitsFacade.updateVisit(eq(99), any())).thenReturn(Optional.of(completed));

        VisitView result = vetSelfService.completeVisit(vet, 99);

        assertThat(result.status()).isEqualTo(VisitStatus.COMPLETED);
        assertThat(result.vetId()).isEqualTo(vet.getId());
        verify(visitsFacade).updateVisit(eq(99), any());
    }
}
