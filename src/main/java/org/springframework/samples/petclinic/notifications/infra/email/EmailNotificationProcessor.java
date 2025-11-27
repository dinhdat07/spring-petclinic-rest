package org.springframework.samples.petclinic.notifications.infra.email;

import java.time.format.DateTimeFormatter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.appointments.events.AppointmentVisitLinkedEvent;
import org.springframework.samples.petclinic.notifications.app.NotificationProcessor;
import org.springframework.samples.petclinic.notifications.domain.NotificationLog;
import org.springframework.samples.petclinic.notifications.infra.repository.NotificationLogRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Slf4j
@RequiredArgsConstructor
public class EmailNotificationProcessor implements NotificationProcessor {

    private final JavaMailSender mailSender;
    private final NotificationEmailProperties properties;
    private final NotificationLogRepository notificationLogRepository;

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    @Retry(name = "rt.notifications.email")
    @CircuitBreaker(name = "cb.notifications.email")
    public void onAppointmentConfirmed(AppointmentConfirmedEvent event) {
        String subject = properties.getSubjectConfirmed()
            .replace("{appointmentId}", value(event.appointmentId()));

        String ownerRecipient = fallbackEmail(event.ownerEmail(), properties.getOwnerRecipient());
        String ownerName = value(event.ownerName());
        String vetName = value(event.vetName());

        String body = """
            Appointment %s has been confirmed.
            Owner: %s (ID %s)
            Pet ID: %s
            Vet: %s (ID %s)
            Status: %s
            Notes: %s
            """.formatted(
            value(event.appointmentId()),
            ownerName,
            value(event.ownerId()),
            value(event.petId()),
            vetName,
            value(event.vetId()),
            event.status(),
            event.triageNotes() != null ? event.triageNotes() : "N/A"
        );

        sendEmail(ownerRecipient, subject, body);
        logEvent(event.appointmentId(), "CONFIRMED");
    }

    @Override
    @Retry(name = "rt.notifications.email")
    @CircuitBreaker(name = "cb.notifications.email")
    public void onVisitLinked(AppointmentVisitLinkedEvent event) {
        String subject = properties.getSubjectVisitLinked()
            .replace("{appointmentId}", value(event.appointmentId()))
            .replace("{visitId}", value(event.visitId()));
        String body = """
            Visit %s has been linked to appointment %s.
            Pet ID: %s
            Vet ID: %s
            """.formatted(
            value(event.visitId()),
            value(event.appointmentId()),
            value(event.petId()),
            value(event.vetId())
        );

        sendEmail(fallbackEmail(event.ownerEmail(), properties.getOwnerRecipient()), subject, body);
        sendEmail(fallbackEmail(event.vetEmail(), properties.getVetRecipient()), subject, body);
        logEvent(event.appointmentId(), "VISIT_LINKED");
    }

    private void sendEmail(String to, String subject, String body) {
        if (to == null || to.isBlank()) {
            throw new AmqpRejectAndDontRequeueException("Missing recipient email");
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom(properties.getFrom());
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("NotificationService - sent email to {} with subject '{}'", to, subject);
        }
        catch (AmqpRejectAndDontRequeueException ex) {
            throw ex;
        }
        catch (MailException ex) {
            log.warn("NotificationService - mail transport failed for {}: {}", to, ex.getMessage());
            throw new AmqpRejectAndDontRequeueException("Email delivery failed", ex);
        }
        catch (Exception ex) {
            log.error("NotificationService - failed to send email to {}", to, ex);
            throw new AmqpRejectAndDontRequeueException("Email delivery failed", ex);
        }
    }

    private void logEvent(Integer appointmentId, String eventType) {
        try {
            notificationLogRepository.save(new NotificationLog(appointmentId, eventType));
        }
        catch (Exception ex) {
            log.warn("NotificationService - failed to persist notification log for appointment {} ({})",
                    appointmentId, eventType, ex);
        }
    }
    private String value(Object obj) {
        return obj != null ? obj.toString() : "N/A";
    }

    private String fallbackEmail(String preferred, String fallback) {
        return (preferred != null && !preferred.isBlank()) ? preferred : fallback;
    }
}
