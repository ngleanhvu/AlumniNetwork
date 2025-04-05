//package com.finalterm.alumninetwork.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//@Configuration
//@EnableWebSecurity
//@EnableTransactionManagement
//@ComponentScan(basePackages = {
//        "com.finalterm.alumninetwork",
//        "com.finalterm.alumninetwork.controller",
//        "com.finalterm.alumninetwork.service",
//        "com.finalterm.alumninetwork.repository"
//})
//public class SecurityConfig {
//
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .securityMatcher("/**")  // Match all other requests
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(authorizeRequests -> {
//                    authorizeRequests
//                            .requestMatchers("/").permitAll()
//                            .requestMatchers("/**").hasRole("ADMIN")
//                            .anyRequest().authenticated();
//                })
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .usernameParameter("username")
//                        .passwordParameter("password")
//                        .defaultSuccessUrl("/", true)
//                        .failureUrl("/login?error")
//                        .permitAll()
//                )
//                .logout(logout -> logout.permitAll());
//
//        return http.build();
//    }
//
//
//}
