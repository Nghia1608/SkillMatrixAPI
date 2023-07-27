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
                        "/swagger-ui.html"
                )
                .permitAll()
                .requestMatchers(POST,"/auth/authenticate/**").permitAll()
                .requestMatchers(POST,"/auth/registerUser/**").hasAnyAuthority("Admin")
                .requestMatchers(POST,"/auth/logout/**").permitAll()
                .requestMatchers(POST,"/participant/submitUserRate/**").hasAnyAuthority("Member")
                .requestMatchers(POST,"/participant/**").hasAnyAuthority("Manager")
                .requestMatchers("/manager/**").hasAnyAuthority("Manager")

                .requestMatchers(GET,"/skill/**").hasAnyAuthority("Manager")
                .requestMatchers(GET,"/skillDomain/**").hasAnyAuthority("Manager")
                .requestMatchers(GET,"/team/**").hasAnyAuthority("Manager")
                .requestMatchers( GET,"/project/**").hasAnyAuthority("Manager")
                .requestMatchers( GET,"/room/**").hasAnyAuthority("Manager")

                .requestMatchers(POST,"/skill/addNewSkill/**").hasAnyAuthority("Admin", "Manager")
                .requestMatchers(DELETE,"/skill/deleteSkill/**").hasAnyAuthority("Admin", "Manager")
                .requestMatchers("/skill/**").hasAnyAuthority("Admin")
                .requestMatchers("/skillDomain/**").hasAnyAuthority("Admin")
                .requestMatchers("/team/**").hasAnyAuthority("Admin")
                .requestMatchers( "/project/**").hasAnyAuthority("Admin")
                .requestMatchers( "/room/**").hasAnyAuthority("Admin")

                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
        ;

        return http.build();
    }
}