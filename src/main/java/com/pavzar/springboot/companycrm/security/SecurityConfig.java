package com.pavzar.springboot.companycrm.security;

import com.pavzar.springboot.companycrm.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService); //set the custom user details service
        auth.setPasswordEncoder(passwordEncoder()); //set the password encoder - bcrypt
        return auth;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationSuccessHandler customAuthenticationSuccessHandler) throws Exception {

        http.authorizeHttpRequests(configurer -> configurer
                        // restrict access based on roles
                        .requestMatchers("/").hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
                        .requestMatchers("/employees/showUpdateUserForm").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/employees/saveUpdatedUser").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/employees/deleteUser").hasRole("ADMIN")
                        .requestMatchers("/employees/changePassword").hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
                        .requestMatchers("/employees/changePasswordProcess").hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
                        .requestMatchers("/register/**").permitAll()
                        // any request must be authenticated
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        // request mapping
                        .loginPage("/showLoginPage")
                        // no controller request mapping required
                        .loginProcessingUrl("/authenticateUser")
                        .successHandler(customAuthenticationSuccessHandler)
                        // everyone can see login page
                        .permitAll()

                )// add logout support
                .logout(LogoutConfigurer::permitAll)
                // configure error page
                .exceptionHandling(configurer ->
                        configurer.accessDeniedPage("/access-denied"));

        return http.build();
    }
}
