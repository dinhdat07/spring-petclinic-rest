package org.springframework.samples.petclinic.scheduling.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.scheduling.SchedulingServiceProperties;
import org.springframework.samples.petclinic.scheduling.infra.repository.AppointmentSlotAllocationRepository;
import org.springframework.samples.petclinic.scheduling.infra.repository.SlotRepository;

@DataJpaTest
@Import({SchedulingAvailabilityService.class, SchedulingAvailabilityServiceTests.TestConfig.class})
class SchedulingAvailabilityServiceTests {

    @Autowired
    private SchedulingAvailabilityService service;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private AppointmentSlotAllocationRepository allocationRepository;

    @Test
    void createsSlotAndAllocationOnConfirmEvent() {
        var event = new AppointmentConfirmedEvent(
            1,
            2,
            3,
            5,
            AppointmentStatus.CONFIRMED,
            "notes",
            LocalDateTime.of(2025, 11, 15, 10, 15)
        );

        service.onAppointmentConfirmed(event);

        assertThat(slotRepository.findAll()).hasSize(1);
        assertThat(allocationRepository.findByAppointmentId(1)).isPresent();
    }

    @Test
    void ignoresDuplicateAllocation() {
        var event = new AppointmentConfirmedEvent(
            1,
            2,
            3,
            5,
            AppointmentStatus.CONFIRMED,
            "notes",
            LocalDateTime.of(2025, 11, 15, 10, 0)
        );

        service.onAppointmentConfirmed(event);
        service.onAppointmentConfirmed(event);

        assertThat(allocationRepository.findAll()).hasSize(1);
    }

    @Test
    void slotsForDayReturnOrderedSlots() {
        service.onAppointmentConfirmed(new AppointmentConfirmedEvent(
            1,
            2,
            3,
            5,
            AppointmentStatus.CONFIRMED,
            "notes",
            LocalDateTime.of(2025, 11, 15, 10, 0)
        ));
        service.onAppointmentConfirmed(new AppointmentConfirmedEvent(
            2,
            2,
            3,
            5,
            AppointmentStatus.CONFIRMED,
            "notes",
            LocalDateTime.of(2025, 11, 15, 11, 0)
        ));

        List<Slot> slots = service.slotsForVetAndDate(5, LocalDate.of(2025, 11, 15));
        assertThat(slots).hasSize(2);
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
