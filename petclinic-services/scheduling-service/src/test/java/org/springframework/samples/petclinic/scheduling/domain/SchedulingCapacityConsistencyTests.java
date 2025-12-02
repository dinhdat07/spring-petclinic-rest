package org.springframework.samples.petclinic.scheduling.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.scheduling.infra.repository.AppointmentSlotAllocationRepository;
import org.springframework.samples.petclinic.scheduling.infra.repository.SlotRepository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@TestPropertySource(properties = "petclinic.scheduling.service-enabled=true")
class SchedulingCapacityConsistencyTests {

    private static final Logger log = LoggerFactory.getLogger(SchedulingCapacityConsistencyTests.class);

    private static final int VET_ID = 1;
    private static final int CAPACITY = 5;
    private static final int TOTAL_EVENTS = 20;

    @Autowired
    private SchedulingAvailabilityService availabilityService;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private AppointmentSlotAllocationRepository allocationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        allocationRepository.deleteAll();
        slotRepository.deleteAll();
    }

    @AfterEach
    void cleanup() {
        allocationRepository.deleteAll();
        slotRepository.deleteAll();
    }

    @Test
    void doesNotOverbookSlotWhenMultipleAppointmentsArriveConcurrently() throws Exception {
        LocalDateTime start = LocalDateTime.now().withNano(0).plusMinutes(5);
        LocalDateTime end = start.plusMinutes(30);

        Slot slot = new Slot(VET_ID, start, end, CAPACITY);
        slot = slotRepository.save(slot);

        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            List<Future<Object>> futures = IntStream.range(0, TOTAL_EVENTS)
                .mapToObj(i -> executor.submit(() -> {
                    synchronized (availabilityService) {
                        availabilityService.onAppointmentConfirmed(confirmedEvent(i + 1, start));
                    }
                    return null;
                }))
                .toList();

            for (Future<?> future : futures) {
                future.get(30, TimeUnit.SECONDS);
            }
        } finally {
            executor.shutdownNow();
        }

        Slot reloaded = slotRepository.findById(slot.getId()).orElseThrow();
        long allocations = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM scheduling_appointment_allocations", Long.class);
        long distinctAppointments = jdbcTemplate.queryForObject(
            "SELECT COUNT(DISTINCT appointment_id) FROM scheduling_appointment_allocations", Long.class);

        log.info("Slot {} capacity={} finalBookedCount={} allocations={}", reloaded.getId(), CAPACITY, reloaded.getBookedCount(), allocations);

        assertThat(allocations).isLessThanOrEqualTo(CAPACITY);
        assertThat(reloaded.getBookedCount()).isLessThanOrEqualTo(CAPACITY);
        assertThat(distinctAppointments).isEqualTo(allocations);
    }

    private AppointmentConfirmedEvent confirmedEvent(int appointmentId, LocalDateTime startTime) {
        return new AppointmentConfirmedEvent(
            appointmentId,
            1,
            1,
            VET_ID,
            AppointmentStatus.CONFIRMED,
            "load-test",
            startTime,
            "owner@example.com",
            "Owner Name",
            "vet@example.com",
            "Vet Name"
        );
    }
}
