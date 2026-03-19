package com.portfolio.blog.config;

import com.portfolio.blog.security.JwtAuthenticationFilter;
import com.portfolio.blog.services.JwtBlacklistServiceInterface;
import com.portfolio.blog.services.JwtServiceInterface;
import io.jsonwebtoken.JwtException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
    Spring security configuration;
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter) throws JwtException {

        try {
            http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(HttpMethod.POST, "/api/registration**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/tags/**").permitAll()
                            .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                            .anyRequest().authenticated()
                    )
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(session ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    ).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
        } catch (JwtException e) {
            throw new JwtException("Security filter chain failed");
        }
    }

    @Bean
    public JwtAuthenticationFilter authenticationFilter(JwtServiceInterface jwtService, JwtBlacklistServiceInterface jwtBlacklistService){
        return new JwtAuthenticationFilter(jwtService, jwtBlacklistService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //  bcrypt by default
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

}
