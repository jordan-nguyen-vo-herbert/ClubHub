package com.clubhub.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.clubhub.entities.Student;
import com.clubhub.security.StudentPrincipal;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

// Runs a test as a signed-in student, the same kind of user the real app signs in (a StudentPrincipal)
// Use it instead of @WithMockUser on any test that loads a page: the shared header shows principal.firstName,
// which Spring's generic mock user doesn't have (FR-010-11)
// The student is made up and never saved, so it has no database ID
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@WithSecurityContext(factory = WithMockStudent.Factory.class)
public @interface WithMockStudent {
    String firstName() default "Test"; // shown in the header as "Signed in as Test"

    String email() default "test.student@wsu.edu";

    // Spring Security calls this before each test to build the signed-in student
    class Factory implements WithSecurityContextFactory<WithMockStudent> {
        @Override
        public SecurityContext createSecurityContext(WithMockStudent annotation) {
            Student student = new Student("000000000", annotation.firstName(), "Student", annotation.email(),
                    null, null, null, null);
            StudentPrincipal principal = new StudentPrincipal(student);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(
                    UsernamePasswordAuthenticationToken.authenticated(principal, null, principal.getAuthorities()));
            return context;
        }
    }
}
