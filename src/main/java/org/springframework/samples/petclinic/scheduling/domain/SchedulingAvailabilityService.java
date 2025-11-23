package org.springframework.samples.petclinic.scheduling.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.scheduling.SchedulingServiceProperties;
import org.springframework.samples.petclinic.scheduling.infra.repository.AppointmentSlotAllocationRepository;
import org.springframework.samples.petclinic.scheduling.infra.repository.SlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "petclinic.scheduling.service-enabled", havingValue = "true")
public class SchedulingAvailabilityService {

    private final SlotRepository slotRepository;
    private final AppointmentSlotAllocationRepository allocationRepository;
    private final SchedulingServiceProperties properties;

    public void onAppointmentConfirmed(AppointmentConfirmedEvent event) {
        if (event == null || event.appointmentId() == null || event.vetId() == null || event.startTime() == null) {
            log.debug("Skipping scheduling update because event data is incomplete: {}", event);
            return;
        }

        if (allocationRepository.findByAppointmentId(event.appointmentId()).isPresent()) {
            log.debug("Appointment {} already allocated to a slot. Skipping.", event.appointmentId());
            return;
        }

        LocalDateTime slotStart = normalizeToSlotStart(event.startTime());
        LocalDateTime slotEnd = slotStart.plusMinutes(properties.getSlotDurationMinutes());
        Slot slot = slotRepository.findByVetIdAndStartTime(event.vetId(), slotStart)
            .orElseGet(() -> slotRepository.save(new Slot(
                event.vetId(),
                slotStart,
                slotEnd,
                properties.getSlotCapacity()
            )));

        slot.incrementBooking();
        slotRepository.save(slot);
        allocationRepository.save(new AppointmentSlotAllocation(event.appointmentId(), slot));
    }

    @Transactional(readOnly = true)
    public int activeAppointmentsForVet(Integer vetId) {
        return totalBookedForVetOn(vetId, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public int totalBookedForVetOn(Integer vetId, LocalDate date) {
        if (vetId == null || date == null) {
            return 0;
        }
        return slotsForVetAndDate(vetId, date).stream()
            .mapToInt(Slot::getBookedCount)
            .sum();
    }

    @Transactional(readOnly = true)
    public int totalCapacityForVetOn(Integer vetId, LocalDate date) {
        if (vetId == null || date == null) {
            return 0;
        }
        return slotsForVetAndDate(vetId, date).stream()
            .mapToInt(Slot::getCapacity)
            .sum();
    }

    @Transactional(readOnly = true)
    public List<Slot> slotsForVetAndDate(Integer vetId, LocalDate date) {
        if (vetId == null || date == null) {
            return List.of();
        }
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(LocalTime.MAX).withNano(0);
        return slotRepository.findByVetIdAndStartTimeBetweenOrderByStartTime(vetId, dayStart, dayEnd);
    }

    private LocalDateTime normalizeToSlotStart(LocalDateTime time) {
        int slotMinutes = Math.max(properties.getSlotDurationMinutes(), 5);
        LocalDateTime truncated = time.truncatedTo(ChronoUnit.MINUTES).withSecond(0).withNano(0);
        int minuteBlock = (truncated.getMinute() / slotMinutes) * slotMinutes;
        return truncated.withMinute(minuteBlock);
    }
}
