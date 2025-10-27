package it.paoloadesso.gestionalesala.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails gestore = User.builder()
                .username("gestore")
                .password(passwordEncoder().encode("gestore"))
                .roles("GESTORE")
                .build();

        UserDetails cassiere = User.builder()
                .username("cassiere")
                .password(passwordEncoder().encode("cassiere"))
                .roles("GESTORE")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("GESTORE")
                .build();

        return new InMemoryUserDetailsManager(gestore, cassiere, admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                // SWAGGER UI
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-gestionaleSala",

                                // API DOCS (CHIAVE!)
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/v3/api-docs",
                                "/api-docs/**",
                                "/api-docs-sala/**",
                                "/api-docs-sala/json",

                                // SWAGGER RESOURCES
                                "/swagger-resources/**",
                                "/swagger-resources",
                                "/swagger-config",
                                "/webjars/**",

                                // ACTUATOR & ALTRI
                                "/actuator/health",
                                "/favicon.ico",
                                "/error"
                        ).permitAll()

                        // SOLO LE API SONO PROTETTE
                        .requestMatchers("/api/**").hasRole("GESTORE")

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()) // BasicAuth attivo per le API
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
