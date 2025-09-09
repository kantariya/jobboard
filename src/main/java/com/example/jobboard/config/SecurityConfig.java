package com.example.jobboard.config;

import com.example.jobboard.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity // Enables method-level security like @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // --- 1. PUBLIC ENDPOINTS (No Authentication Required) ---
                        // Anyone can register a new user account.
                        .requestMatchers(HttpMethod.POST, "/api/profile/register").permitAll()
                        // Anyone can view public lists of jobs, companies, and posts.
                        .requestMatchers(HttpMethod.GET, "/api/jobs/**", "/api/companies/public/**", "/api/posts/public/**").permitAll()

                        // --- 2. RECRUITER ONLY ENDPOINTS (Authentication + 'ROLE_RECRUITER' required) ---
                        // Creating, updating, or deleting companies.
                        .requestMatchers("/api/companies/**").hasAuthority("ROLE_RECRUITER")
                        // Creating, updating, or deleting jobs.
                        .requestMatchers(HttpMethod.POST, "/api/jobs").hasAuthority("ROLE_RECRUITER")
                        .requestMatchers(HttpMethod.PUT, "/api/jobs/**").hasAuthority("ROLE_RECRUITER")
                        .requestMatchers(HttpMethod.DELETE, "/api/jobs/**").hasAuthority("ROLE_RECRUITER")
                        // Viewing or updating applications for a job.
                        .requestMatchers("/api/jobs/*/applications", "/api/applications/*/status").hasAuthority("ROLE_RECRUITER")

                        // --- 3. GENERAL AUTHENTICATED ENDPOINTS (Any Logged-in User) ---
                        // All other endpoints under /api/ require the user to be authenticated,
                        // regardless of their role. This covers:
                        // - User profile management (/api/profile)
                        // - All Connection actions (/api/connections/**)
                        // - All Post actions (creating, editing, feed) (/api/posts/**)
                        // - All Application actions (applying, viewing own, withdrawing)
                        .requestMatchers("/api/**").authenticated()

                        // --- 4. DEFAULT RULE ---
                        // Secure any other endpoint by default.
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}

