# Implementation Plan

## Tasks

### FR-008.1 Back End: Feedback (#64, Yohann)
- [ ] `FeedbackCategory`: `BUG_REPORT`, `SUGGESTION` and `OTHER`, each with the name the form shows
- [ ] `Feedback`: category, message (up to 1000 characters), the sending student and the time sent, set when it's created
- [ ] `FeedbackRepository`: `CrudRepository<Feedback, Long>`, plus `findAllByOrderBySubmittedAtDesc()` for reviewing
- [ ] `FeedbackForm`: the `category` and `message` fields, with no sender field (NFR-008-01)
- [ ] `FeedbackController`: `GET /feedback` shows the form with `${categoryOptions}`. `POST /feedback` runs `checkFeedback`, shows the form again on errors, and otherwise stores the feedback and redirects to `/feedback` with `feedbackSent`
- [ ] `checkFeedback`: a missing category, an empty or blank message, and a message over 1000 characters, each attached to its field (FR-008-04, FR-008-05)
- [ ] `messages.properties`: `typeMismatch.category` for a category that isn't in the list (FR-008-06)
- [ ] `feedback.html`: a plain page wired to the fixed names in `design.md`, with the header, the confirmation, both fields with their errors, and the "Send feedback" button
- [ ] White-box tests for each FR-008.1 scenario (`FeedbackBackEndTest`)
- [ ] Check by hand: send feedback, send an empty form, send a long message, and open `/feedback` signed out

### FR-008.2 Front End: Feedback form (#65, TBD)
- [ ] `feedback.html`: style the plain page from FR-008.1, keeping its field names, messages and links, and mark the required fields
- [ ] `fragments/nav.html`: a "Feedback" link for signed-in users (FR-006-01 lists the header links)
- [ ] Check by hand in a browser: the form, each error message, and the confirmation

### Tests
- [ ] `validation.md` for this folder (TBD)
- [ ] Automated tests from `validation.md` (TBD)

## Files to Create or Modify

All paths are under `code/backend/`.

- `src/main/java/com/clubhub/entities/FeedbackCategory.java` (new): the feedback categories
- `src/main/java/com/clubhub/entities/Feedback.java` (new): one stored piece of feedback
- `src/main/java/com/clubhub/dto/FeedbackForm.java` (new): what the form sends
- `src/main/java/com/clubhub/repository/FeedbackRepository.java` (new): saving and listing feedback
- `src/main/java/com/clubhub/controller/FeedbackController.java` (new): the feedback page and its checks
- `src/main/resources/templates/feedback.html` (new): the feedback page, a plain version from FR-008.1 that FR-008.2 styles
- `src/main/resources/messages.properties`: the message for an unknown category
- `src/main/resources/templates/fragments/nav.html` (FR-008.2): the "Feedback" link
- `src/test/java/com/clubhub/FeedbackTests/FeedbackBackEndTest.java` (new): white-box tests for the back end

## Dependencies

- **Libraries:** none new. Spring Web MVC, Spring Data JPA and Spring Security are already in `pom.xml`.
- **Features:**
  - FR-010 User Authentication (#5): the signed-in student who sends the feedback, and the redirect to sign-in for signed-out visitors.
  - FR-006 Navigation and error pages (#10): the header on the feedback page, and the "Feedback" link added in FR-008.2.
  - DB-1 Data: PostgreSQL (#54): where the feedback is stored for review.
- **Work that depends on this feature:**
  - FR-009 User Roles and Permissions (#14) could add a page where administrators review feedback.
