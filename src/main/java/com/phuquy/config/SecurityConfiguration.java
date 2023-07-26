package com.phuquy.config;


import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/swagger-ui.html",
                        "/auth/authenticate/**"
                )
                .permitAll()
                .requestMatchers(POST,"/auth/registerUser").hasAnyAuthority("Admin")
                .requestMatchers("/auth/**").authenticated()
                .requestMatchers("/participant/submitUserRate/**").authenticated()
                .requestMatchers(POST,"/participant/**").hasAnyAuthority("Manager")
                .requestMatchers("/manager/**").hasAnyAuthority("Manager")
                .requestMatchers(POST,"/skill/**").hasAnyAuthority("Admin")
                .requestMatchers(POST,"/skillDomain/**").hasAnyAuthority("Admin")
                .requestMatchers(POST,"/team/**").hasAnyAuthority("Admin")
                .requestMatchers(POST, "/project/**").hasAnyAuthority("Admin")
                .requestMatchers("/skill/**").hasAnyAuthority("Manager")
                .requestMatchers("/skillDomain/**").hasAnyAuthority("Manager")
                .requestMatchers("/team/**").hasAnyAuthority("Manager")
                .requestMatchers( "/project/**").hasAnyAuthority("Manager")
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }
}