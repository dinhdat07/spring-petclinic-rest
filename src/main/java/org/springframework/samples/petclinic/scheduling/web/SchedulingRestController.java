package org.springframework.samples.petclinic.scheduling.web;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.samples.petclinic.scheduling.domain.SchedulingAvailabilityService;

@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(value = "petclinic.scheduling.service-enabled", havingValue = "true")
@RequestMapping("/api/scheduling/vets")
public class SchedulingRestController {

    private final SchedulingAvailabilityService availabilityService;

    @GetMapping("/{vetId}/capacity")
    public Map<String, Integer> capacity(@PathVariable Integer vetId) {
        return Map.of("vetId", vetId, "activeAppointments", availabilityService.activeAppointmentsForVet(vetId));
    }
}
