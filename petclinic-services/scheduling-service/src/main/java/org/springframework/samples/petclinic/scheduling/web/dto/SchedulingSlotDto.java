package org.springframework.samples.petclinic.scheduling.web.dto;

import java.time.LocalDateTime;

import org.springframework.samples.petclinic.scheduling.domain.Slot;
import org.springframework.samples.petclinic.scheduling.domain.SlotStatus;

public record SchedulingSlotDto(
    Long id,
    Integer vetId,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Integer capacity,
    Integer bookedCount,
    SlotStatus status
) {

    public static SchedulingSlotDto from(Slot slot) {
        return new SchedulingSlotDto(
            slot.getId(),
            slot.getVetId(),
            slot.getStartTime(),
            slot.getEndTime(),
            slot.getCapacity(),
            slot.getBookedCount(),
            slot.getStatus()
        );
    }
}
