package org.springframework.samples.petclinic.scheduling.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "scheduling_slots",
    uniqueConstraints = @UniqueConstraint(name = "uk_scheduling_slot_vet_time", columnNames = {"vet_id", "start_time"})
)
@Getter
@Setter
@NoArgsConstructor
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vet_id", nullable = false)
    private Integer vetId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "booked_count", nullable = false)
    private Integer bookedCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SlotStatus status = SlotStatus.OPEN;

    public Slot(Integer vetId, LocalDateTime startTime, LocalDateTime endTime, Integer capacity) {
        this.vetId = vetId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.bookedCount = 0;
        this.status = SlotStatus.OPEN;
    }

    public void incrementBooking() {
        this.bookedCount = this.bookedCount + 1;
        if (this.bookedCount >= this.capacity) {
            this.status = SlotStatus.FULL;
        }
    }

    public void decrementBooking() {
        if (this.bookedCount > 0) {
            this.bookedCount = this.bookedCount - 1;
        }
        if (this.bookedCount < this.capacity) {
            this.status = SlotStatus.OPEN;
        }
    }
}
