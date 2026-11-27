package com.clubhub.entities;

// What a piece of feedback is about, chosen from a dropdown on the feedback form (FR-008-01)
public enum FeedbackCategory {
    BUG_REPORT("Bug report"),
    SUGGESTION("Suggestion"),
    OTHER("Other");

    private final String categoryDescription;

    FeedbackCategory(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    // The name the form shows, e.g. "Bug report"
    public String getCategory() {
        return this.categoryDescription;
    }
}
