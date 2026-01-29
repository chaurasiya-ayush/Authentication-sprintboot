package com.auth.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // 🔥 CORS ENABLE
            .cors(Customizer.withDefaults())

            // ❌ CSRF disable (JWT / REST API ke liye)
            .csrf(csrf -> csrf.disable())

            // 🔐 Authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/api/auth/**"   // register, login, verify, refresh
                ).permitAll()
                .anyRequest().authenticated()
            )
             // ❌ basic auth nahi chahiye
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    // 🔥🔥🔥 THIS IS THE MOST IMPORTANT PART (CORS CONFIG)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:3000")); // Next.js
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
