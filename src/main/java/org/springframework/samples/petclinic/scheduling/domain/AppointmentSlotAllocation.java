package org.springframework.samples.petclinic.scheduling.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "scheduling_appointment_allocations",
    uniqueConstraints = @UniqueConstraint(name = "uk_scheduling_allocation_appointment", columnNames = "appointment_id")
)
@Getter
@Setter
@NoArgsConstructor
public class AppointmentSlotAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_id", nullable = false)
    private Integer appointmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private Slot slot;

    public AppointmentSlotAllocation(Integer appointmentId, Slot slot) {
        this.appointmentId = appointmentId;
        this.slot = slot;
    }
}
