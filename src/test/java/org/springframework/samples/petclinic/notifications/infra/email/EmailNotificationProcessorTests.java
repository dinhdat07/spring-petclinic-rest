package org.springframework.samples.petclinic.notifications.infra.email;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.samples.petclinic.appointments.api.AppointmentStatus;
import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;
import org.springframework.samples.petclinic.appointments.events.AppointmentVisitLinkedEvent;
import org.springframework.samples.petclinic.notifications.app.NotificationProcessor;

@ExtendWith(MockitoExtension.class)
class EmailNotificationProcessorTests {

    @Mock
    private JavaMailSender mailSender;

    private NotificationEmailProperties emailProperties;

    private NotificationProcessor processor;

    @BeforeEach
    void setUp() {
        emailProperties = new NotificationEmailProperties();
        emailProperties.setEnabled(true);
        emailProperties.setOwnerRecipient("owner@test.local");
        emailProperties.setVetRecipient("vet@test.local");
        processor = new EmailNotificationProcessor(mailSender, emailProperties);
    }

    @Test
    void sendsEmailOnAppointmentConfirmed() {
        AppointmentConfirmedEvent event = new AppointmentConfirmedEvent(1, 2, 3, 4, AppointmentStatus.CONFIRMED, "triaged");

        processor.onAppointmentConfirmed(event);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage message = captor.getValue();
        assert message != null;
        org.assertj.core.api.Assertions.assertThat(message.getTo()).contains("owner@test.local");
        org.assertj.core.api.Assertions.assertThat(message.getSubject()).contains("1");
    }

    @Test
    void throwsWhenMailSenderFails() {
        AppointmentVisitLinkedEvent event = new AppointmentVisitLinkedEvent(1, 7, 2, 3, 4);
        doThrow(new MailException("SMTP down") {
            private static final long serialVersionUID = 1L;
        }).when(mailSender).send(any(SimpleMailMessage.class));

        assertThatThrownBy(() -> processor.onVisitLinked(event))
            .isInstanceOf(org.springframework.amqp.AmqpRejectAndDontRequeueException.class);
    }
}
