package org.springframework.samples.petclinic;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.appointments.messaging.AppointmentMessagingProperties;

/**
 * Integration-style load test for measuring throughput of the notifications and scheduling workers.
 *
 * This test expects running RabbitMQ and the worker JVMs (notifications & scheduling) to be started manually.
 * Enable with -DenableLoadTests=true (guarded to avoid failing normal builds).
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("load")
@EnabledIfSystemProperty(named = "enableLoadTests", matches = "true")
class ScalabilityThroughputIT {

    private static final Logger log = LoggerFactory.getLogger(ScalabilityThroughputIT.class);

    /**
     * Configure number of events via system property "loadTest.numEvents"; defaults to 500.
     */
    private static final int NUM_EVENTS = Integer.getInteger("loadTest.numEvents", 500);

    private static final Duration TIMEOUT = Duration.ofMinutes(1);
    private static final Duration POLL_INTERVAL = Duration.ofMillis(500);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AppointmentMessagingProperties messagingProperties;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void measuresThroughputAcrossWorkers() {
        long initialNotificationCount = countNotifications();
        long initialAllocations = countSchedulingAllocations();

        Instant start = Instant.now();
        publishConfirmedEvents(NUM_EVENTS);

        waitForCount("notifications", this::countNotifications, initialNotificationCount + NUM_EVENTS);
        waitForCount("scheduling allocations", this::countSchedulingAllocations, initialAllocations + NUM_EVENTS);
        Duration elapsed = Duration.between(start, Instant.now());

        double seconds = Math.max(1.0, elapsed.toMillis() / 1000.0);
        double throughput = NUM_EVENTS / seconds;

        log.info("Load test finished: events={}, durationSec={}, throughput={} events/sec", NUM_EVENTS,
                String.format("%.2f", seconds),
                String.format("%.2f", throughput));

        assertThat(elapsed).isLessThan(TIMEOUT);
        assertThat(countNotifications() - initialNotificationCount).isGreaterThanOrEqualTo(NUM_EVENTS);
        assertThat(countSchedulingAllocations() - initialAllocations).isGreaterThanOrEqualTo(NUM_EVENTS);
    }

    private void publishConfirmedEvents(int count) {
        for (int i = 0; i < count; i++) {
            int id = i + 1;
            AppointmentConfirmedEvent event = new AppointmentConfirmedEvent(
                    id,
                    /* ownerId */ 1,
                    /* petId */ 1,
                    /* vetId */ 1,
                    org.springframework.samples.petclinic.appointments.api.AppointmentStatus.CONFIRMED,
                    "load-test",
                    java.time.LocalDateTime.now().plusMinutes(30),
                    "owner@example.com",
                    "Owner Name",
                    "vet@example.com",
                    "Vet Name"
            );
            rabbitTemplate.convertAndSend(
                    messagingProperties.getExchange(),
                    messagingProperties.getConfirmedRoutingKey(),
                    event
            );
        }
    }

    private void waitForCount(String label, CountSupplier supplier, long target) {
        Instant deadline = Instant.now().plus(TIMEOUT);
        while (Instant.now().isBefore(deadline)) {
            long current = supplier.get();
            if (current >= target) {
                log.info("Reached target for {}: {}/{}", label, current, target);
                return;
            }
            sleep(POLL_INTERVAL);
        }
        long finalCount = supplier.get();
        throw new AssertionError(
                "Timeout waiting for " + label + " to reach " + target + " (final=" + finalCount + ")");
    }

    private long countNotifications() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM notification_log", Long.class);
    }

    private long countSchedulingAllocations() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM scheduling_appointment_allocations", Long.class);
    }

    private void sleep(Duration duration) {
        try {
            TimeUnit.MILLISECONDS.sleep(duration.toMillis());
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Interrupted while waiting", e);
        }
    }

    @FunctionalInterface
    private interface CountSupplier {
        long get();
    }
}
