package com.workflowpro.backend.config;
import com.workflowpro.backend.auth.filter.JwtAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // ==================================================
    // PASSWORD ENCODER
    // ==================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ==================================================
    // CORS CONFIGURATION
    // ==================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "https://workflowpro-ui.vercel.app"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept"
                )
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    // ==================================================
    // JWT FILTER REGISTRATION
    // ==================================================

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter>
    jwtFilterRegistration(
            JwtAuthenticationFilter filter
    ) {

        FilterRegistrationBean<JwtAuthenticationFilter>
                registration =
                new FilterRegistrationBean<>(filter);

        /*
         * Disable normal servlet registration.
         *
         * The JWT filter is added manually below
         * using addFilterBefore().
         */
        registration.setEnabled(false);

        return registration;
    }

    // ==================================================
    // SECURITY FILTER CHAIN
    // ==================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // ==================================================
                // CORS
                // ==================================================

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                // ==================================================
                // CSRF
                // ==================================================

                /*
                 * WorkFlowPro uses JWT authentication
                 * with a REST API.
                 *
                 * CSRF is therefore disabled for this
                 * stateless API.
                 */
                .csrf(csrf ->
                        csrf.disable()
                )

                // ==================================================
                // HTTP BASIC
                // ==================================================

                /*
                 * Disable Spring Boot's default
                 * username/password Basic authentication.
                 */
                .httpBasic(httpBasic ->
                        httpBasic.disable()
                )

                // ==================================================
                // FORM LOGIN
                // ==================================================

                /*
                 * Disable Spring Security's default
                 * HTML login page.
                 *
                 * WorkFlowPro has its own React login page.
                 */
                .formLogin(formLogin ->
                        formLogin.disable()
                )

                // ==================================================
                // LOGOUT
                // ==================================================

                /*
                 * Logout is handled by the React frontend
                 * by removing the JWT from localStorage.
                 */
                .logout(logout ->
                        logout.disable()
                )

                // ==================================================
                // SESSION MANAGEMENT
                // ==================================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // ==================================================
                // AUTHORIZATION RULES
                // ==================================================

                .authorizeHttpRequests(auth ->
                        auth

                                // --------------------------------------------------
                                // LOGIN + REGISTRATION
                                // --------------------------------------------------

                                .requestMatchers(
                                        "/api/auth/**"
                                )
                                .permitAll()

                                // --------------------------------------------------
                                // CORS PREFLIGHT
                                // --------------------------------------------------

                                .requestMatchers(
                                        HttpMethod.OPTIONS,
                                        "/**"
                                )
                                .permitAll()

                                // --------------------------------------------------
                                // ADMIN APIs
                                // --------------------------------------------------

                                .requestMatchers(
                                        "/api/admin/**"
                                )
                                .hasRole("ADMIN")

                                // --------------------------------------------------
                                // ALL OTHER APIs
                                // --------------------------------------------------

                                .anyRequest()
                                .authenticated()
                )

                // ==================================================
                // JWT AUTHENTICATION FILTER
                // ==================================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
