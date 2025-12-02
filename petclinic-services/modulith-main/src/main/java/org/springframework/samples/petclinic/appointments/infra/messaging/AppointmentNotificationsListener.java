package org.springframework.samples.petclinic.appointments.infra.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.appointments.events.AppointmentVisitLinkedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(
    value = "petclinic.messaging.appointments.internal-notifications-consumer-enabled",
    havingValue = "true",
    matchIfMissing = true
)
@RabbitListener(queues = "${petclinic.messaging.appointments.notifications-queue:appointments.notifications.q}")
public class AppointmentNotificationsListener {

    private final AppointmentNotificationsProcessor processor;

    @RabbitHandler
    public void handleAppointmentConfirmed(AppointmentConfirmedEvent event) {
        process(() -> processor.onAppointmentConfirmed(event), event.appointmentId(), "confirmed");
    }

    @RabbitHandler
    public void handleVisitLinked(AppointmentVisitLinkedEvent event) {
        process(() -> processor.onVisitLinked(event), event.appointmentId(), "visit-linked");
    }

    @RabbitHandler(isDefault = true)
    public void handleUnknown(Object payload) {
        log.warn("Discarding unknown appointment notification payload type: {}", payload);
    }

    private void process(Runnable action, Integer appointmentId, String eventType) {
        try {
            action.run();
        }
        catch (Exception ex) {
            log.error("Failed to process {} notification for appointment {}", eventType, appointmentId, ex);
            throw new AmqpRejectAndDontRequeueException("Failed to process appointment notification event", ex);
        }
    }
}
