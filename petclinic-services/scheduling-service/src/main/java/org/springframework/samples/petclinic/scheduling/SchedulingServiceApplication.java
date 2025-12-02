package org.springframework.samples.petclinic.scheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class SchedulingServiceApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SchedulingServiceApplication.class);
        app.setAdditionalProfiles("scheduling-service");
        app.setDefaultProperties(Map.of("petclinic.scheduling.service-enabled", "true"));
        app.run(args);
    }
}
