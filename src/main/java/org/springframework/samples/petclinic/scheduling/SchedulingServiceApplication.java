package org.springframework.samples.petclinic.scheduling;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication(scanBasePackages = "org.springframework.samples.petclinic.scheduling")
public class SchedulingServiceApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SchedulingServiceApplication.class);
        app.setDefaultProperties(Map.of("petclinic.scheduling.service-enabled", "true"));
        app.run(args);
    }
}
