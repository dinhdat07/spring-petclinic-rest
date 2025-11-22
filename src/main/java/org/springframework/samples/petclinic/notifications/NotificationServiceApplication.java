package org.springframework.samples.petclinic.notifications;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.springframework.samples.petclinic.notifications")
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(NotificationServiceApplication.class);
        app.setAdditionalProfiles("notifications-service");
        app.setDefaultProperties(Map.of("petclinic.notifications.service-enabled", "true"));
        app.run(args);
    }
}
