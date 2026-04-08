package org.springframework.samples.petclinic;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = {
		"org.springframework.samples.petclinic.appointments",
		"org.springframework.samples.petclinic.authentication",
		"org.springframework.samples.petclinic.catalog",
		"org.springframework.samples.petclinic.iam",
		"org.springframework.samples.petclinic.owners",
		"org.springframework.samples.petclinic.platform",
		"org.springframework.samples.petclinic.vets",
		"org.springframework.samples.petclinic.visits"
})
@EnableCaching
public class PetClinicApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(PetClinicApplication.class, args);
	}
}
