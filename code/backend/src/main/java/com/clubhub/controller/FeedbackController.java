package com.clubhub.controller;

import com.clubhub.dto.FeedbackForm;
import com.clubhub.entities.Feedback;
import com.clubhub.entities.FeedbackCategory;
import com.clubhub.entities.Student;
import com.clubhub.repository.FeedbackRepository;
import com.clubhub.repository.StudentRepository;
import com.clubhub.security.StudentPrincipal;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Feedback about ClubHub, FR-008.1 (specs/features/feedback/)
// /feedback needs a signed-in student: SecurityConfig sends signed-out visitors to /login first (FR-008-07)
@Controller // Tells Spring that this handles feedback requests and returns Thymeleaf pages
public class FeedbackController {
    // Longest message the database column holds, Feedback.message is @Column(length = 1000) (FR-008-05)
    public static final int MAX_MESSAGE_LENGTH = 1000;

    private final FeedbackRepository feedbackRepository;
    private final StudentRepository studentRepository; // to look up the signed-in student who sends the feedback

    public FeedbackController(FeedbackRepository feedbackRepository, StudentRepository studentRepository) { // Spring passes in both repositories automatically
        this.feedbackRepository = feedbackRepository;
        this.studentRepository = studentRepository;
    }

    // Dropdown options for the category, available in the template as ${categoryOptions}
    // Spring runs this before every request in this controller, so the list is there even when the form is shown again with errors
    @ModelAttribute("categoryOptions")
    public FeedbackCategory[] categoryOptions() {
        return FeedbackCategory.values(); // every category, in the order they're declared in the enum
    }

    @GetMapping("/feedback") // GET: user opens the feedback page, also where they land after sending feedback
    public String feedbackForm(Model model) {
        model.addAttribute("feedbackForm", new FeedbackForm()); // blank form object, the template's th:object="${feedbackForm}" binds to it
        return "feedback"; // renders templates/feedback.html
    }

    @PostMapping("/feedback") // POST: user clicks "Send feedback"
    public String feedbackSubmit(@ModelAttribute FeedbackForm form, BindingResult result, // result holds form errors, must come right after the form
            @AuthenticationPrincipal StudentPrincipal principal, RedirectAttributes redirectAttributes) {
        Student submitter = signedInStudent(principal);
        checkFeedback(form, result);
        // Any error (a category Spring couldn't convert, or a check in checkFeedback) -> show the form again with what they typed
        if (result.hasErrors()) {
            return "feedback";
        }
        feedbackRepository.save(new Feedback(form.getCategory(), form.getMessage().trim(), submitter));
        redirectAttributes.addFlashAttribute("feedbackSent", true); // reaches the next page only, feedback.html shows the confirmation (FR-008-03)
        return "redirect:/feedback"; // a fresh, empty form, and a refresh can't send the feedback again
    }

    /**
     * Checks feedback on the server, so a request that skips the browser's checks is still refused (NFR-008-02).
     * Each problem is attached to its own field, so the form shows it next to that field (FR-008-04, FR-008-05).
     * A category that isn't in the list never reaches this method as a value: Spring records it as a
     * conversion error, whose message is in messages.properties (FR-008-06).
     *
     * @param form   what the user submitted
     * @param result where field errors are recorded
     */
    private void checkFeedback(FeedbackForm form, BindingResult result) {
        if (form.getCategory() == null && !result.hasFieldErrors("category")) {
            result.rejectValue("category", "required", "Choose a category");
        }

        String message = form.getMessage() == null ? "" : form.getMessage().trim();
        if (message.isEmpty()) {
            result.rejectValue("message", "required", "Enter your feedback");
        } else if (message.length() > MAX_MESSAGE_LENGTH) {
            result.rejectValue("message", "tooLong", "Keep your feedback to " + MAX_MESSAGE_LENGTH + " characters or fewer");
        }
    }

    // The Student behind the signed-in user, the only place a sender comes from, never a value the browser sends (NFR-008-01)
    // SecurityConfig already turned signed-out visitors away, so this only fails if the profile is missing:
    // HTTP 403 instead of storing feedback without a sender
    private Student signedInStudent(StudentPrincipal principal) {
        return Optional.ofNullable(principal)
                .map(StudentPrincipal::getStudentId)
                .flatMap(studentRepository::findById)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "No profile for the signed-in student"));
    }
}
