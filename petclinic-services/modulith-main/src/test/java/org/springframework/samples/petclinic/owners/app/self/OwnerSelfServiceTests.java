package org.springframework.samples.petclinic.owners.app.self;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;
import org.springframework.samples.petclinic.appointments.api.AppointmentView;
import org.springframework.samples.petclinic.appointments.api.AppointmentsFacade;
import org.springframework.samples.petclinic.owners.app.owner.OwnerService;
import org.springframework.samples.petclinic.owners.domain.Owner;
import org.springframework.samples.petclinic.owners.domain.Pet;
import org.springframework.samples.petclinic.owners.web.self.dto.OwnerAppointmentRequest;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class OwnerSelfServiceTests {

    @Mock
    private OwnerService ownerService;

    @Mock
    private AppointmentsFacade appointmentsFacade;

    @InjectMocks
    private OwnerSelfService ownerSelfService;

    private Owner owner;
    private Pet pet;

    @BeforeEach
    void setUp() {
        owner = new Owner();
        owner.setId(1);
        owner.setUsername("owner");

        pet = new Pet();
        pet.setId(5);
        pet.setName("Snowball");
        pet.setOwner(owner);
        pet.setTypeId(2);
        pet.setBirthDate(java.time.LocalDate.now().minusYears(2));

        owner.addPet(pet);
    }

    @Test
    void scheduleCreatesPendingAppointmentForOwnedPet() {
        LocalDateTime futureTime = LocalDateTime.now().plusDays(2);
        AppointmentView saved = new AppointmentView(
            99,
            owner.getId(),
            pet.getId(),
            null,
            futureTime,
            AppointmentStatus.PENDING,
            "Routine check",
            futureTime.minusDays(1),
            futureTime.minusDays(1),
            null,
            null
        );

        when(appointmentsFacade.create(any())).thenReturn(saved);

        OwnerAppointmentRequest request = new OwnerAppointmentRequest(pet.getId(), futureTime, "Routine check");

        AppointmentView result = ownerSelfService.schedule(owner, request);

        assertThat(result.id()).isEqualTo(99);
        assertThat(result.status()).isEqualTo(AppointmentStatus.PENDING);
        verify(appointmentsFacade).create(any());
    }

    @Test
    void scheduleRejectsPetNotOwnedByCurrentOwner() {
        OwnerAppointmentRequest request = new OwnerAppointmentRequest(999, LocalDateTime.now().plusDays(1), "Invalid");

        assertThatThrownBy(() -> ownerSelfService.schedule(owner, request))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Pet does not belong");
    }

    @Test
    void cancelPendingAppointmentMarksAsCancelled() {
        LocalDateTime startTime = LocalDateTime.now().plusDays(3);
        AppointmentView existing = new AppointmentView(
            10,
            owner.getId(),
            pet.getId(),
            1,
            startTime,
            AppointmentStatus.PENDING,
            "Routine check",
            startTime.minusDays(1),
            startTime.minusDays(1),
            null,
            null
        );

        AppointmentView cancelledView = new AppointmentView(
            existing.id(),
            owner.getId(),
            pet.getId(),
            1,
            startTime,
            AppointmentStatus.CANCELLED,
            existing.notes(),
            existing.createdAt(),
            LocalDateTime.now(),
            null,
            null
        );

        when(appointmentsFacade.findForOwner(existing.id(), owner.getId()))
            .thenReturn(Optional.of(existing));
        when(appointmentsFacade.update(anyInt(), any()))
            .thenReturn(Optional.of(cancelledView));

        AppointmentView cancelled = ownerSelfService.cancel(owner, existing.id());

        assertThat(cancelled.status()).isEqualTo(AppointmentStatus.CANCELLED);
        verify(appointmentsFacade).update(anyInt(), any());
    }

    @Test
    void cancelRejectsConfirmedAppointmentInside24Hours() {
        AppointmentView existing = new AppointmentView(
            11,
            owner.getId(),
            pet.getId(),
            1,
            LocalDateTime.now().plusHours(2),
            AppointmentStatus.CONFIRMED,
            null,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(1),
            null,
            null
        );

        when(appointmentsFacade.findForOwner(existing.id(), owner.getId()))
            .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> ownerSelfService.cancel(owner, existing.id()))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Confirmed appointments can only be cancelled");
    }

    @Test
    void cancelCompletedAppointmentThrows() {
        AppointmentView existing = new AppointmentView(
            12,
            owner.getId(),
            pet.getId(),
            1,
            LocalDateTime.now().plusDays(1),
            AppointmentStatus.COMPLETED,
            null,
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now().minusDays(1),
            null,
            null
        );

        when(appointmentsFacade.findForOwner(existing.id(), owner.getId()))
            .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> ownerSelfService.cancel(owner, existing.id()))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Completed appointments cannot be cancelled");
    }

    @Test
    void requireCurrentOwnerFetchesOwnerByUsername() {
        when(ownerService.findByUsername("owner")).thenReturn(Optional.of(owner));

        Owner result = ownerSelfService.requireCurrentOwner("owner");

        assertThat(result).isSameAs(owner);
    }
}
