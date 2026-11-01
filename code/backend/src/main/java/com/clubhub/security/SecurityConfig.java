package com.clubhub.security;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.csrf.CsrfException;

@Configuration // Tells Spring this class defines beans, Spring Security picks up the SecurityFilterChain below
public class SecurityConfig {
    // Session attribute holding the email from the last failed sign-in, read by the sign-in page
    public static final String LAST_EMAIL_ATTRIBUTE = "lastSignInEmail";

    // Home, sign-in, create a profile (GET and POST), the stylesheet, and error pages like the 404
    private static final String[] PUBLIC_PAGES = {"/", "/login", "/studentForm", "/styles.css", "/error"};

    // Spring's standard failure handling: redirect to /login?error and remember the error for the page
    private final SimpleUrlAuthenticationFailureHandler toErrorPage = new SimpleUrlAuthenticationFailureHandler("/login?error");

    // Spring's standard refusal: HTTP 403, which shows templates/error.html
    private final AccessDeniedHandler send403 = new AccessDeniedHandlerImpl();

    @Bean // The rules every request passes through before it reaches a controller
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Which pages need a signed-in student (FR-010-05 in specs/features/sign-in/requirements.md)
            // Rules are checked top to bottom and the first match wins, so anyRequest() has to stay last
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_PAGES).permitAll()
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
            )
            // What a refused request gets back, see handleRefused below
            .exceptionHandling(errors -> errors.accessDeniedHandler(this::handleRefused));
        return http.build();
    }

    // A form left open after signing out or a session timeout has a stale CSRF token, and the CSRF check runs before
    // the sign-in check. So a signed-out visitor posting to a page that needs sign-in is sent to sign in (FR-002-04).
    // The request is refused and nothing is saved either way, and everyone else still gets the 403 (NFR-010-03)
    private void handleRefused(HttpServletRequest request, HttpServletResponse response, AccessDeniedException refused)
            throws IOException, ServletException {
        boolean signedOut = SecurityContextHolder.getContext().getAuthentication() == null; // nothing is set yet at the CSRF check
        String page = request.getRequestURI().substring(request.getContextPath().length());
        if (refused instanceof CsrfException && signedOut && !List.of(PUBLIC_PAGES).contains(page)) {
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            send403.handle(request, response, refused);
        }
    }

    @Bean // BCrypt hashes passwords when they're saved and checks them at sign-in (FR-010-02)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
