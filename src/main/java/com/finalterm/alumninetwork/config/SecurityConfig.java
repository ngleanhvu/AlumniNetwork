package com.finalterm.alumninetwork.config;

import com.finalterm.alumninetwork.filter.CustomAccessDeniedHandler;
import com.finalterm.alumninetwork.filter.JwtAuthenticationTokenFilter;
import com.finalterm.alumninetwork.filter.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@EnableTransactionManagement
@ComponentScan(basePackages = {
        "com.finalterm.alumninetwork",
        "com.finalterm.alumninetwork.controller",
        "com.finalterm.alumninetwork.service",
        "com.finalterm.alumninetwork.repository",
        "com.finalterm.alumninetwork.component",
        "com.finalterm.alumninetwork.filter"
})
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(authorizeRequests -> {
                    authorizeRequests

                            .requestMatchers("/api/users/login", "/api/swagger-ui.html", "/api/users/register").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/comments").permitAll()
                            .requestMatchers(HttpMethod.DELETE, "/api/users/delete/**").permitAll()
//                            .requestMatchers(HttpMethod.DELETE, "/api/**").hasAnyRole("ADMIN", "LECTURER", "ALUMNI")
//                            .requestMatchers(HttpMethod.POST, "/api/**").hasAnyRole("ADMIN", "LECTURER", "ALUMNI")
//                            .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("ADMIN", "LECTURER", "ALUMNI")
//                            .requestMatchers(HttpMethod.PUT, "/api/**").hasAnyRole("ADMIN", "LECTURER", "ALUMNI")
//                            .requestMatchers(HttpMethod.PATCH, "/api/**").hasAnyRole("ADMIN", "LECTURER", "ALUMNI")
                            .anyRequest().authenticated();
                })
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")  // Match all other requests
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorizeRequests -> {
                    authorizeRequests
                            .requestMatchers("/").permitAll()
                            .requestMatchers("/**").hasRole("ADMIN")
                            .anyRequest().authenticated();
                })
                .formLogin(form -> form
                        .loginPage("/users/admin/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/users/admin/login?error")
                        .permitAll()
                )
                .logout(logout -> logout.permitAll());

        return http.build();
    }


}
