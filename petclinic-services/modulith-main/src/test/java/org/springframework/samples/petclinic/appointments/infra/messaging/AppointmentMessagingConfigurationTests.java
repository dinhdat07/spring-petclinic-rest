package org.springframework.samples.petclinic.appointments.infra.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.samples.petclinic.appointments.messaging.AppointmentMessagingProperties;

class AppointmentMessagingConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(
            JacksonAutoConfiguration.class,
            RabbitAutoConfiguration.class,
            AppointmentMessagingConfiguration.class
        ))
        .withPropertyValues(
            "spring.rabbitmq.host=localhost",
            "spring.rabbitmq.port=5672",
            "spring.rabbitmq.admin.auto-startup=false",
            "spring.rabbitmq.listener.simple.auto-startup=false",
            "spring.rabbitmq.listener.direct.auto-startup=false"
        );

    @Test
    void declaresExchangeQueuesAndBindings() {
        contextRunner.run(context -> {
            TopicExchange exchange = context.getBean(TopicExchange.class);
            assertThat(exchange.getName()).isEqualTo("petclinic.appointments.exchange");

            AppointmentMessagingProperties properties = context.getBean(AppointmentMessagingProperties.class);
            assertThat(properties.getNotificationsQueue()).isEqualTo("appointments.notifications.q");

            Declarables declarables = context.getBean("appointmentQueuesAndBindings", Declarables.class);
            assertThat(declarables.getDeclarables())
                .filteredOn(Queue.class::isInstance)
                .map(Queue.class::cast)
                .extracting(Queue::getName)
                .contains(
                    properties.getNotificationsQueue(),
                    properties.getNotificationsDlq(),
                    properties.getAvailabilityQueue(),
                    properties.getAvailabilityDlq()
                );
        });
    }
}
