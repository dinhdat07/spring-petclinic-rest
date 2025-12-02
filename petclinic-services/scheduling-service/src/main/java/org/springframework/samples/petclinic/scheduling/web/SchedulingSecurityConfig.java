package org.springframework.samples.petclinic.scheduling.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("scheduling-service")
public class SchedulingSecurityConfig {

    @Bean
    SecurityFilterChain schedulingFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/scheduling/**", "/actuator/**").permitAll()
                        .anyRequest().authenticated());
        return http.build();
    }
}
