package org.springframework.samples.petclinic.appointments.infra.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(
    value = "petclinic.messaging.appointments.internal-availability-consumer-enabled",
    havingValue = "true",
    matchIfMissing = true
)
@RabbitListener(queues = "${petclinic.messaging.appointments.availability-queue:appointments.availability.q}")
public class AppointmentAvailabilityListener {

    private final AppointmentAvailabilityProcessor processor;

    @RabbitHandler
    public void handleAppointmentConfirmed(AppointmentConfirmedEvent event) {
        process(() -> processor.onAppointmentConfirmed(event), event.appointmentId());
    }

    @RabbitHandler(isDefault = true)
    public void handleUnknownPayload(Object payload) {
        log.warn("Discarding unsupported appointment availability payload: {}", payload);
    }

    private void process(Runnable action, Integer appointmentId) {
        try {
            action.run();
        }
        catch (Exception ex) {
            log.error("Failed to process availability update for appointment {}", appointmentId, ex);
            throw new AmqpRejectAndDontRequeueException("Availability processing failed", ex);
        }
    }
}
