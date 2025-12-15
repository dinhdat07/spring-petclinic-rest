package org.springframework.samples.petclinic.scheduling.web;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.scheduling.domain.SchedulingAvailabilityService;
import org.springframework.samples.petclinic.scheduling.web.dto.SchedulingSlotDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(value = "petclinic.scheduling.service-enabled", havingValue = "true")
@RequestMapping("/api/scheduling/vets")
public class SchedulingRestController {

    private final SchedulingAvailabilityService availabilityService;

    @GetMapping("/{vetId}/capacity")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "capacityfallbackMethod")
    @Retry(name = "myRetry")
    public Map<String, Object> capacity(
            @PathVariable Integer vetId,
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        int booked = availabilityService.totalBookedForVetOn(vetId, targetDate);
        int capacity = availabilityService.totalCapacityForVetOn(vetId, targetDate);
        return Map.of(
                "vetId", vetId,
                "date", targetDate,
                "booked", booked,
                "capacity", capacity,
                "remaining", Math.max(capacity - booked, 0));
    }

    @GetMapping("/{vetId}/slots")
    @CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "slotfallbackMethod")
    @Retry(name = "myRetry")
    public List<SchedulingSlotDto> slots(
            @PathVariable Integer vetId,
            @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return availabilityService.slotsForVetAndDate(vetId, date).stream()
                .map(SchedulingSlotDto::from)
                .toList();
    }

    public Map<String, Object> capacityfallbackMethod(Integer vetId, LocalDate date, Throwable t) {
        return Map.of(
                "message", "Service temporarily unavailable, please try again later",
                "status", "503 Service Unavailable");
    }

    public List<SchedulingSlotDto> slotfallbackMethod(Integer vetId, LocalDate date, Throwable t) {
        return List.of();
    }
}
