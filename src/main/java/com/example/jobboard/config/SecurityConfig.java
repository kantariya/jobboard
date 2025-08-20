package com.example.jobboard.config;


import com.example.jobboard.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for simplicity in testing (adjust for production)
                .csrf(csrf -> csrf.disable())

                // Set URL-based security rules
                .authorizeHttpRequests(auth -> auth
                        // Company endpoints: GET permitted to all, others require recruiter role
                        .requestMatchers(HttpMethod.GET, "/companies/**").permitAll()
                        .requestMatchers("/companies/**").hasAuthority("ROLE_RECRUITER")// Applies to POST, PUT, DELETE

                        // Job endpoints: GET permitted to all; posting new jobs require recruiter role
                        .requestMatchers(HttpMethod.GET, "/jobs/**").permitAll()
                        .requestMatchers("/jobs/**").hasAuthority("ROLE_RECRUITER")


                        // Post endpoints: GET permitted to all; other operations require any authenticated user
                        .requestMatchers("/posts").permitAll() // GET /posts open to all
                        .requestMatchers("/posts/**").authenticated()

                        // User registration should be open
                        .requestMatchers("/users/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()

                        // Any other requests require authentication
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("{\"message\": \"User logged out successfully\"}");
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )

                // Use HTTP Basic Authentication (suitable for testing via Postman)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    // Define the PasswordEncoder bean using BCrypt
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
