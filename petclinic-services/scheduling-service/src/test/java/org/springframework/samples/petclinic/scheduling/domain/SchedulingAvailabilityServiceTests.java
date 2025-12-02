package org.springframework.samples.petclinic.scheduling.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.scheduling.SchedulingServiceProperties;
import org.springframework.samples.petclinic.scheduling.infra.repository.AppointmentSlotAllocationRepository;
import org.springframework.samples.petclinic.scheduling.infra.repository.SlotRepository;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({SchedulingAvailabilityService.class, SchedulingAvailabilityServiceTests.TestConfig.class})
@TestPropertySource(properties = "petclinic.scheduling.service-enabled=true")
class SchedulingAvailabilityServiceTests {

    @Autowired
    private SchedulingAvailabilityService service;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private AppointmentSlotAllocationRepository allocationRepository;

    @Test
    void createsSlotAndAllocationOnConfirmEvent() {
        var event = confirmedEvent(1, 5, LocalDateTime.of(2025, 11, 15, 10, 15));

        service.onAppointmentConfirmed(event);

        assertThat(slotRepository.findAll()).hasSize(1);
        assertThat(allocationRepository.findByAppointmentId(1)).isPresent();
    }

    @Test
    void ignoresDuplicateAllocation() {
        var event = confirmedEvent(1, 5, LocalDateTime.of(2025, 11, 15, 10, 0));

        service.onAppointmentConfirmed(event);
        service.onAppointmentConfirmed(event);

        assertThat(allocationRepository.findAll()).hasSize(1);
    }

    @Test
    void slotsForDayReturnOrderedSlots() {
        service.onAppointmentConfirmed(confirmedEvent(1, 5, LocalDateTime.of(2025, 11, 15, 10, 0)));
        service.onAppointmentConfirmed(confirmedEvent(2, 5, LocalDateTime.of(2025, 11, 15, 11, 0)));

        List<Slot> slots = service.slotsForVetAndDate(5, LocalDate.of(2025, 11, 15));
        assertThat(slots).hasSize(2);
    }

    private AppointmentConfirmedEvent confirmedEvent(int appointmentId, Integer vetId, LocalDateTime startTime) {
        return new AppointmentConfirmedEvent(
            appointmentId,
            2,
            3,
            vetId,
            AppointmentStatus.CONFIRMED,
            "notes",
            startTime,
            "owner@example.com",
            "Owner Name",
            "vet@example.com",
            "Vet Name"
        );
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        SchedulingServiceProperties schedulingServiceProperties() {
            SchedulingServiceProperties properties = new SchedulingServiceProperties();
            properties.setSlotDurationMinutes(30);
            properties.setSlotCapacity(2);
            return properties;
        }
    }
}
