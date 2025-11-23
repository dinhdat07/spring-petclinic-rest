package org.springframework.samples.petclinic.scheduling.infra.messaging;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.samples.petclinic.appointments.messaging.AppointmentMessagingProperties;
import org.springframework.samples.petclinic.scheduling.SchedulingServiceProperties;

@Configuration
@EnableRabbit
@ConditionalOnProperty(value = "petclinic.scheduling.service-enabled", havingValue = "true")
@EnableConfigurationProperties({AppointmentMessagingProperties.class, SchedulingServiceProperties.class})
public class SchedulingMessagingConfiguration {
}
