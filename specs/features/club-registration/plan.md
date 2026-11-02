# Implementation Plan

## Tasks

### FR-002.7 Back End: Signed-in club registration (#50, Jordan)
- [x] `ClubForm`: remove `founderStudentNumber`, because the founder now comes from sign-in
- [x] `ClubController`: a private `signedInStudent(principal)` that loads the `Student` for the principal's `studentId`, or fails with HTTP 403
- [x] `ClubController.clubForm`: take the signed-in student and add `founderName` and `founderEmail` to the model
- [x] `ClubController.clubFormSubmit`: take the founder from the signed-in student, keep the Pending status and the President membership, add the `registered` flash attribute and redirect to `/clubs/{clubID}`. When the form has errors, show it again with the founder fields
- [x] `ClubRepository`: add `findByFounderIdOrderByClubIDDesc(Long founderId)`
- [x] `ClubController.myClubs`: `GET /clubs/mine` shows `myClubs.html` with the signed-in student's clubs as `clubs`
- [x] `clubForm.html`: replace the "Founder's WSU ID" field with the founder's name and email as plain text
- [x] `club.html`: when `registered` is set, a plain message that the registration was submitted and is waiting for approval, with a link to `/clubs/mine`
- [x] `myClubs.html` (new, plain): each club's name, linked to its page, and its status. With no clubs, "No club requests yet" and a link to `/clubForm`
- [x] `ClubRegistrationTest`: sign in as the saved test student instead of using `@WithMockStudent`, stop sending `founderStudentNumber`, and replace `registrationFailsWithInvalidStudentID` with a check that a WSU ID sent with the form is ignored
- [x] `SecurityConfig`: send a signed-out visitor's stale form to `/login`, and keep the 403 for every other refused request
- [x] White-box tests for each back-end scenario (`SignedInClubRegistrationTest`)
- [ ] Tell Tirth that `ClubRegistrationTest`, from TEST-010, changed

### FR-002.8 Front End: Confirmation and My Club Requests (#58, Daniel)
- [ ] `club.html`: style the success message from FR-002.7, keeping `registered` and the link
- [ ] `myClubs.html`: style the list and the empty state, keeping the model attribute `clubs` and the links

### Coordination with other features
- [x] FR-010.1 (#49): merged in PR #75, and `main` is merged into this branch. This branch's pull request targets `main`
- [ ] FR-002.6 (#52, Daniel): adds fields and checks to `ClubForm`, `ClubController.clubFormSubmit`, `clubForm.html` and `club.html`, the same places this feature changes. Agree which merges first. The other one merges `main` in and keeps both changes
- [x] FR-006.1 (#56, Yohann): merged in PR #79. `clubForm.html` and `club.html` have the shared header, and `myClubs.html` has it too
- [ ] Header link to My Club Requests (Yohann's `fragments/nav.html`): today the only way to reach the page is the link in the success message
- [ ] TEST-012 (#61, Daniel): `@WithMockStudent` has no saved profile, so `/clubForm` and `/clubs/mine` refuse it with HTTP 403. Checks of those pages sign in with `with(user(new StudentPrincipal(savedStudent)))`
- [ ] FR-009.1 (#62, Sprint 3): when pending clubs are hidden from other students, keep showing a pending club to its founder ("Submitter can view their pending club")

### Tests
- [x] `validation.md` for this folder (TEST-012, #61, Daniel)
- [x] Automated tests from `validation.md` (TEST-012, #61, Daniel)

## Files to Create or Modify

All paths are under `code/backend/`.

- `src/main/java/com/clubhub/dto/ClubForm.java`: remove `founderStudentNumber`
- `src/main/java/com/clubhub/controller/ClubController.java`: the signed-in founder, the `registered` flash attribute and `GET /clubs/mine`
- `src/main/java/com/clubhub/repository/ClubRepository.java`: `findByFounderIdOrderByClubIDDesc`
- `src/main/java/com/clubhub/security/SecurityConfig.java`: the redirect for a signed-out visitor's stale form
- `src/main/resources/templates/clubForm.html`: the founder's name and email instead of the WSU ID field
- `src/main/resources/templates/club.html`: the success message after registering
- `src/main/resources/templates/myClubs.html` (new): My Club Requests, a plain version that Daniel styles in FR-002.8
- `src/test/java/com/clubhub/ClubTests/ClubRegistrationTest.java`: sign in as a real student, and the ignored WSU ID check
- `src/test/java/com/clubhub/ClubTests/SignedInClubRegistrationTest.java` (new): white-box tests for the back-end scenarios

## Dependencies

- **Libraries:** none new. `@AuthenticationPrincipal` and `spring-security-test` come with FR-010.1.
- **Features:**
  - FR-010.1 Back End: Sign-in (#49): the signed-in student (`StudentPrincipal`), and the sign-in requirement that covers the signed-out scenarios.
  - FR-001 User Profile Creation and FR-002.1 to FR-002.5 (Sprint 1): the student profiles, the club entity and the club pages this builds on.
- **Work that depends on this feature:**
  - FR-002.8 Front End: Confirmation and My Club Requests (#58) styles the confirmation and the list.
  - FR-002.6 Back End: Registration edge cases (#52) adds fields to the same form.
  - FR-009.1 Back End: Roles and club approval (#62) hides pending clubs from everyone except their founder.
