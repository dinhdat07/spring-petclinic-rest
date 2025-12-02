package org.springframework.samples.petclinic.scheduling.infra.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.scheduling.domain.SchedulingAvailabilityService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "petclinic.scheduling.enabled", havingValue = "true", matchIfMissing = true)
@RabbitListener(queues = "${petclinic.messaging.appointments.availability-queue:appointments.availability.q}")
public class SchedulingEventListener {

    private final SchedulingAvailabilityService availabilityService;

    @RabbitHandler
    public void handleAppointmentConfirmed(AppointmentConfirmedEvent event) {
        try {
            availabilityService.onAppointmentConfirmed(event);
        }
        catch (Exception ex) {
            log.error("SchedulingService - failed to update capacity for appointment {}", event.appointmentId(), ex);
            throw new AmqpRejectAndDontRequeueException("Scheduling processing failed", ex);
        }
    }

    @RabbitHandler(isDefault = true)
    public void handleUnknown(Object payload) {
        log.warn("SchedulingService - unsupported payload {}", payload);
    }
}
