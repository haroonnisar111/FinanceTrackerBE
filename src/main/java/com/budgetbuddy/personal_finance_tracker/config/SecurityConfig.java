package com.budgetbuddy.personal_finance_tracker.config;
import com.budgetbuddy.personal_finance_tracker.security.JwtAuthenticationEntryPoint;
import com.budgetbuddy.personal_finance_tracker.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints - Authentication
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh").permitAll()
                        .requestMatchers("/api/auth/forgot-password", "/api/auth/reset-password").permitAll()

                        // Public endpoints - Health and monitoring
                        .requestMatchers("/actuator/health", "/actuator/info", "/actuator/metrics").permitAll()

                        // Swagger and API documentation
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/webjars/**").permitAll()

                        // Static resources
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()

                        // Transaction endpoints - User access only
                        .requestMatchers(HttpMethod.GET, "/api/transactions/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/transactions").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/transactions/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/transactions/**").hasRole("USER")

                        // Budget endpoints - User access only
                        .requestMatchers(HttpMethod.GET, "/api/budgets/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/budgets").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/budgets/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/budgets/**").hasRole("USER")

                        // Category endpoints - User access
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/categories").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("USER")

                        // Report endpoints - User access only
                        .requestMatchers("/api/reports/**").hasRole("USER")

                        // User profile management
                        .requestMatchers(HttpMethod.GET, "/api/users/profile").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/users/profile").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/profile").hasRole("USER")

                        // Admin endpoints (future use)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                );

        // Add JWT filter before username/password authentication filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow specific origins in production, all in development
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:3000"      // React development server

        ));

        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));

        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Requested-With", "Accept",
                "Origin", "Cache-Control", "Content-Range", "X-File-Name"
        ));

        configuration.setExposedHeaders(Arrays.asList(
                "Content-Range", "X-Content-Range", "Authorization"
        ));

        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
