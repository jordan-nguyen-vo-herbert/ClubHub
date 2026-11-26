package com.clubhub.dto;

import com.clubhub.entities.FeedbackCategory;

// This class takes input from a signed-in user sending feedback about ClubHub (FR-008-01)
// No constructor, Spring uses Java's default one (new FeedbackForm()) then calls the setters
// Not included: the sender and the time sent. The sender is always the signed-in student,
// so a value sent with the form has nowhere to go (NFR-008-01)
public class FeedbackForm {
    private FeedbackCategory category; // required
    private String message; // required, up to 1000 characters

    // Setters

    public void setCategory(FeedbackCategory category) {
        this.category = category;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Getters

    public FeedbackCategory getCategory() {
        return category;
    }

    public String getMessage() {
        return message;
    }
}
