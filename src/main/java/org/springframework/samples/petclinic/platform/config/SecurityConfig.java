package org.springframework.samples.petclinic.platform.config;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.samples.petclinic.platform.props.CorsProps;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
@ConditionalOnProperty(name = "petclinic.security.enable", havingValue = "false", matchIfMissing = true)
public class SecurityConfig {
  @Bean
  SecurityFilterChain chain(HttpSecurity http) throws Exception {
    http
        .cors(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(registry -> registry.anyRequest().permitAll());

    return http.build();
  }

  @Bean
  CorsConfigurationSource corsSource(CorsProps props) {
    var c = new CorsConfiguration();
    c.setAllowedOrigins(List.of(props.getAllowedOrigins()));
    c.setAllowedMethods(List.of(props.getAllowedMethods()));
    c.setAllowedHeaders(List.of(props.getAllowedHeaders()));
    c.setExposedHeaders(List.of(props.getExposedHeaders()));
    c.setAllowCredentials(true);
    c.setMaxAge(props.getMaxAge());
    
    var s = new UrlBasedCorsConfigurationSource();
    s.registerCorsConfiguration("/api/**", c);
    return s;
  }
}
