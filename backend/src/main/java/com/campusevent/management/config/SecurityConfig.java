package com.campusevent.management.config;

import com.campusevent.management.security.JwtAuthenticationEntryPoint;
import com.campusevent.management.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter authenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        // Options preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Authentication endpoints
                        .requestMatchers("/api/auth/**").permitAll()

                        // Public Event browsing
                        .requestMatchers(HttpMethod.GET, "/api/events/**").permitAll()

                        // Faculty only endpoints
                        .requestMatchers("/api/faculty/**").hasRole("FACULTY")
                        .requestMatchers(HttpMethod.POST, "/api/events").hasRole("FACULTY")
                        .requestMatchers(HttpMethod.PUT, "/api/events/**").hasRole("FACULTY")
                        .requestMatchers(HttpMethod.DELETE, "/api/events/**").hasRole("FACULTY")

                        // Student endpoints
                        .requestMatchers("/api/students/**").hasAnyRole("STUDENT", "FACULTY")
                        .requestMatchers(HttpMethod.POST, "/api/events/*/register").hasRole("STUDENT")
                        .requestMatchers("/api/registrations/**").hasAnyRole("STUDENT", "FACULTY")

                        // Error endpoint
                        .requestMatchers("/error").permitAll()

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
