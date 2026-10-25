package com.clubhub.controller;

import com.clubhub.dto.StudentForm;
import com.clubhub.dto.StudentResponse;
import com.clubhub.entities.Gender;
import com.clubhub.entities.Pronouns;
import com.clubhub.entities.Student;
import com.clubhub.repository.StudentRepository;
import com.clubhub.security.StudentPrincipal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller // Tells Spring that this handles student-related requests and returns Thymeleaf pages
public class StudentController {
    private StudentRepository studentRepository;
    private PasswordEncoder passwordEncoder; // BCrypt, defined in SecurityConfig

    // Signing someone in from code (instead of the sign-in form) means saving the security context ourselves
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public StudentController(StudentRepository studentRepository, PasswordEncoder passwordEncoder) { // Spring passes both in automatically
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/students/{id}") // Maps the endpoint to the URL path "/students/{id}"
    public String studentProfile(@PathVariable Long id, Model model) { // uses id of type long to identify the student
        // No match -> HTTP 404, Spring shows templates/error/404.html
        Student match = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        // have a match, copy the safe fields into the DTO (no password)
        StudentResponse student = new StudentResponse(match.getId(), match.getStudentNumber(), match.getFirstName(), match.getLastName(),
                match.getEmail(), match.getMajor(), match.getGender(), match.getPronouns());

        model.addAttribute("student", student); // available in the template as ${student}
        return "student-profile"; // renders templates/student-profile.html
    }

    // Dropdown options, Spring runs these before every request in this controller and adds the result to the Model
    // Named ...Options so they don't get mixed up with the form's own fields (${pronounOptions} is the list, *{pronouns} is the choice)
    @ModelAttribute("genderOptions") // available in the template as ${genderOptions}
    public Gender[] genderOptions() {
        return Gender.values(); // every Gender, in the order they're declared in the enum
    }

    @ModelAttribute("pronounOptions") // available in the template as ${pronounOptions}
    public Pronouns[] pronounOptions() {
        return Pronouns.values();
    }

    @GetMapping("/studentForm") // GET: user opens the create-profile page
    public String studentForm(Model model) { // Spring passes in an empty Model
        model.addAttribute("studentForm", new StudentForm()); // blank form object, the template's th:object="${studentForm}" binds to it
        return "studentForm"; // renders templates/studentForm.html (Yohann)
    }

    @PostMapping("/studentForm") // POST: user clicks submit on the form
    public String studentFormSubmit(@ModelAttribute StudentForm form, BindingResult result, // result holds form errors, must come right after the form
                                    HttpServletRequest request, HttpServletResponse response) { // needed to sign the new student in
        // WSU ID already taken -> attach an error to the WSU ID field instead of crashing on save (studentNumber is unique)
        // May need to handle this later as offering password reset
        if (studentRepository.findByStudentNumber(form.getStudentNumber()).isPresent()) {
            result.rejectValue("studentNumber", "duplicate", "A profile already exists for this WSU ID");
        }

        // The email is the sign-in name (FR-010-03): a WSU address, stored in lowercase, one profile each
        String email = Student.normalizeEmail(form.getEmail());
        if (!email.matches("[^@\\s]+@wsu\\.edu")) {
            result.rejectValue("email", "notWsu", "Use your WSU email, ending in @wsu.edu");
        } else if (studentRepository.findByEmail(email).isPresent()) {
            result.rejectValue("email", "duplicate", "A profile already exists for this email");
        }

        // The browser requires a password, this guards against a request that skips the form
        if (form.getPassword() == null || form.getPassword().isBlank()) {
            result.rejectValue("password", "required", "Enter a password");
        }

        // Any error -> show the form again with what they typed, the template shows it with th:errors="*{field}"
        if (result.hasErrors()) {
            return "studentForm";
        }

        // 1. Convert: form DTO -> entity (Student has no setters, so use the constructor)
        //    The password is hashed with BCrypt here, so the typed password is never stored (FR-010-02)
        Student student = new Student(form.getStudentNumber(), form.getFirstName(), form.getLastName(),
                email, form.getMajor(), form.getGender(), form.getPronouns(), passwordEncoder.encode(form.getPassword()));

        // 2. Save: the database assigns the id, save() returns the stored copy with it filled in
        Student saved = studentRepository.save(student);

        // 3. Sign the new student in, so they land on their profile already signed in (FR-010-04)
        signIn(saved, request, response);

        // 4. Redirect: send the browser to the new profile page
        return "redirect:/students/" + saved.getId();
    }

    @GetMapping("/profile") // GET: the "My student profile" link on the home page, needs a signed-in student
    public String myProfile(@AuthenticationPrincipal StudentPrincipal principal) { // Spring passes in the signed-in student
        // Signed-out visitors never get here, SecurityConfig sends them to /login first (FR-010-09)
        if (principal == null) {
            return "redirect:/login";
        }
        return "redirect:/students/" + principal.getStudentId();
    }

    // Does what the sign-in form does after a correct password, for a student who just created a profile
    private void signIn(Student student, HttpServletRequest request, HttpServletResponse response) {
        StudentPrincipal principal = new StudentPrincipal(student);
        principal.eraseCredentials(); // the session never needs the password hash (NFR-010-01)
        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(principal, null, principal.getAuthorities());

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);

        // A new session ID on sign-in stops someone who knew the old ID from riding along (session fixation)
        if (request.getSession(false) != null) {
            request.changeSessionId();
        }
        // Spring Security 6 only remembers the sign-in across requests once the context is saved to the session
        securityContextRepository.saveContext(context, request, response);
    }

}
