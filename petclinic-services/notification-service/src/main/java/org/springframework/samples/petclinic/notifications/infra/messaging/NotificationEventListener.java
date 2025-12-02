package org.springframework.samples.petclinic.notifications.infra.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.samples.petclinic.notifications.app.NotificationProcessor;
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.appointments.events.AppointmentVisitLinkedEvent;
import org.springframework.stereotype.Component;

import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(value = "petclinic.notifications.service-enabled", havingValue = "true", matchIfMissing = true)
@RabbitListener(queues = "${petclinic.messaging.appointments.notifications-queue:appointments.notifications.q}")
public class NotificationEventListener {

    private final NotificationProcessor processor;
    private final MeterRegistry meterRegistry;

    @RabbitHandler
    @Retry(name = "rt.notifications.email")
    public void handleConfirmed(AppointmentConfirmedEvent event) {
        handle(() -> processor.onAppointmentConfirmed(event), event.appointmentId(), "confirmed");
    }

    @RabbitHandler
    @Retry(name = "rt.notifications.email")
    public void handleVisitLinked(AppointmentVisitLinkedEvent event) {
        handle(() -> processor.onVisitLinked(event), event.appointmentId(), "visit-linked");
    }

    @RabbitHandler(isDefault = true)
    public void handleUnknown(Object payload) {
        log.warn("NotificationService - ignoring unexpected payload {}", payload);
    }

    private void handle(Runnable runnable, Integer appointmentId, String type) {
        try {
            runnable.run();
        }
        catch (Exception ex) {
            log.error("NotificationService - failed to process {} event for appointment {}", type, appointmentId, ex);
            recordDlq(type);
            if (ex instanceof AmqpRejectAndDontRequeueException) {
                throw ex;
            }
            throw new AmqpRejectAndDontRequeueException("Notification processing failed", ex);
        }
    }

    private void recordDlq(String type) {
        if (meterRegistry != null) {
            meterRegistry.counter("notifications.dlq.count", Tags.of(Tag.of("type", type))).increment();
        }
    }
}
