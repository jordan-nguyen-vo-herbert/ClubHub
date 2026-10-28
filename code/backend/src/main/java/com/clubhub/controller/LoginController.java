package com.clubhub.controller;

import com.clubhub.security.SecurityConfig;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Shows the sign-in page, Spring Security itself handles the POST /login that the form sends
public class LoginController {

    @GetMapping("/login") // GET: the sign-in page, also reached as /login?error after a failed sign-in
    public String login(HttpSession session, Model model) {
        // After a failed sign-in, SecurityConfig kept the typed email in the session, hand it to the page once (FR-010-07)
        Object lastEmail = session.getAttribute(SecurityConfig.LAST_EMAIL_ATTRIBUTE);
        if (lastEmail != null) {
            model.addAttribute("email", lastEmail); // available in the template as ${email}
            session.removeAttribute(SecurityConfig.LAST_EMAIL_ATTRIBUTE);
        }
        return "login"; // renders templates/login.html
    }
}
