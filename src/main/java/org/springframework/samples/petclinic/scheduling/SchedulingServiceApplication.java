package org.springframework.samples.petclinic.scheduling;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "org.springframework.samples.petclinic.scheduling",
        "org.springframework.samples.petclinic.platform"
})
public class SchedulingServiceApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SchedulingServiceApplication.class);
        app.setAdditionalProfiles("scheduling-service");
        app.setDefaultProperties(Map.of("petclinic.scheduling.service-enabled", "true"));
        app.run(args);
    }
}
