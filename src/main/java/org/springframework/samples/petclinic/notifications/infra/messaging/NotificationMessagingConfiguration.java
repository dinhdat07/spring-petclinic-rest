package org.springframework.samples.petclinic.notifications.infra.messaging;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.samples.petclinic.appointments.messaging.AppointmentMessagingProperties;
import org.springframework.samples.petclinic.notifications.NotificationServiceProperties;
import org.springframework.samples.petclinic.notifications.app.LoggingNotificationProcessor;
import org.springframework.samples.petclinic.notifications.app.NotificationProcessor;
import org.springframework.samples.petclinic.notifications.infra.email.EmailNotificationProcessor;
import org.springframework.samples.petclinic.notifications.infra.email.NotificationEmailProperties;

@Configuration
@EnableRabbit
@ConditionalOnProperty(value = "petclinic.notifications.service-enabled", havingValue = "true")
@EnableConfigurationProperties({
    AppointmentMessagingProperties.class,
    NotificationServiceProperties.class,
    NotificationEmailProperties.class
})
public class NotificationMessagingConfiguration {

    @Bean
    @ConditionalOnProperty(value = "petclinic.notifications.email.enabled", havingValue = "true")
    NotificationProcessor emailNotificationProcessor(
        JavaMailSender mailSender,
        NotificationEmailProperties emailProperties
    ) {
        return new EmailNotificationProcessor(mailSender, emailProperties);
    }

    @Bean
    @ConditionalOnMissingBean(NotificationProcessor.class)
    NotificationProcessor notificationProcessorFallback() {
        return new LoggingNotificationProcessor();
    }
}
