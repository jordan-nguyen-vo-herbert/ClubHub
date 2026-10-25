package com.clubhub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration // Tells Spring this class defines beans, Spring Security picks up the SecurityFilterChain below
public class SecurityConfig {
    // Session attribute holding the email from the last failed sign-in, read by the sign-in page
    public static final String LAST_EMAIL_ATTRIBUTE = "lastSignInEmail";

    // Spring's standard failure handling: redirect to /login?error and remember the error for the page
    private final SimpleUrlAuthenticationFailureHandler toErrorPage = new SimpleUrlAuthenticationFailureHandler("/login?error");

    @Bean // The rules every request passes through before it reaches a controller
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Which pages need a signed-in student (FR-010-05 in specs/features/sign-in/requirements.md)
            // Rules are checked top to bottom and the first match wins, so anyRequest() has to stay last
            .authorizeHttpRequests(auth -> auth
                // Home, sign-in, create a profile (GET and POST), the stylesheet, and error pages like the 404
                .requestMatchers("/", "/login", "/studentForm", "/styles.css", "/error").permitAll()
                .anyRequest().authenticated() // every other page needs a signed-in student
            )
            // Sign-in form: POST /login with the fields "email" and "password" (fixed names in design.md)
            .formLogin(form -> form
                .loginPage("/login") // our page (LoginController, templates/login.html) instead of Spring's generated one
                .usernameParameter("email") // Spring's default field name is "username"
                .failureHandler((request, response, exception) -> {
                    // Keep the typed email in the session (never the URL) so the sign-in page can refill it (FR-010-07)
                    request.getSession().setAttribute(LAST_EMAIL_ATTRIBUTE, request.getParameter("email"));
                    toErrorPage.onAuthenticationFailure(request, response, exception); // then redirect to /login?error
                })
                .permitAll()
            )
            // Sign-out: POST /logout ends the session, then the browser goes to the home page (FR-010-08)
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );
        return http.build();
    }

    @Bean // BCrypt hashes passwords when they're saved and checks them at sign-in (FR-010-02)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
