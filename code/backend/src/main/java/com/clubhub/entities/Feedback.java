package com.clubhub.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

// One piece of feedback a signed-in user sent about ClubHub, stored for the developers to review (FR-008-02)
// Not shown on any page: developers read it in the database (NFR-008-04)
@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    // EnumType.STRING stores the name ("BUG_REPORT"), not the position, so reordering the enum is safe
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackCategory category;
    @Column(length = 1000, nullable = false) // matches FeedbackController.MAX_MESSAGE_LENGTH, default is only 255
    private String message;
    // The signed-in student who sent it, never a value from the form (NFR-008-01)
    @ManyToOne
    @JoinColumn(name = "submitter_id", nullable = false)
    private Student submitter;
    // When it was sent, set once when the Feedback is created
    @Column(nullable = false)
    private Instant submittedAt;

    // Usage of default constructor  Java persistence API (JPA)
    protected Feedback() {}

    /**
     * Creates feedback from a checked feedback form. The time it was sent is recorded here,
     * so every stored piece of feedback has one.
     *
     * @param category  what the feedback is about
     * @param message   the feedback itself, already checked and trimmed
     * @param submitter the signed-in student who sent it
     */
    public Feedback(FeedbackCategory category, String message, Student submitter) {
        this.category = category;
        this.message = message;
        this.submitter = submitter;
        this.submittedAt = Instant.now();
    }

    public Long getId() {
        return this.id;
    }

    public FeedbackCategory getCategory() {
        return this.category;
    }

    public String getMessage() {
        return this.message;
    }

    public Student getSubmitter() {
        return this.submitter;
    }

    public Instant getSubmittedAt() {
        return this.submittedAt;
    }
}
