# Implementation Plan

## Tasks

### FR-004.1 Back End: Manage members (#12, TBD)
- [ ] `ClubRole`: check its values. It needs `PRESIDENT`, `OFFICER` and `MEMBER`, plus an `isOfficer()` method that is true for `PRESIDENT` and `OFFICER`. Add what is missing
- [ ] `Member`: check how a student is linked to a club today (its own table, or a list inside `Club`) and keep that shape. A student has at most one `Member` per club
- [ ] `ClubController` / club registration: check that the founder is saved as `PRESIDENT` (`ClubRegistrationTest` should show it) and fix it if not
- [ ] `RequestStatus` (new, `entities`): `PENDING` for now
- [ ] `JoinRequest` (new, `entities`): a student's pending request to join a club, with at most one pending request per student and club
- [ ] `JoinRequestRepository` (new, `repository`): lookups by club, and by club and student
- [ ] `MemberRepository` (new, `repository`): only if `Member` is its own entity today and has no repository. It needs a lookup by club and student and a delete by club and student
- [ ] `MemberForm` (new, `dto`): the `wsuId` field with `@NotBlank`, written like `ClubForm` and `StudentForm`
- [ ] `MemberResponse` (changed, `dto`): add what the page needs for each row (the student's ID for the "Remove" link, WSU ID, name and role), if it does not have it already
- [ ] `JoinRequestResponse` (new, `dto`): what the page shows for each pending request
- [ ] `MemberController` (new, `controller`): the member management page and the add, remove, request and accept actions (routes in `design.md`). It takes the acting student from the signed-in user (FR-010.1), never from a form field or URL, and redirects to `/clubs/{id}/members` after every successful change
- [ ] Officer check: one private `requireOfficer(club, student)` method in `MemberController` that throws `ResponseStatusException(403)`, called first by every officer-only action. The project has no service layer yet, so this follows how `ClubController` and `StudentController` already work
- [ ] Errors on the member page: a blank WSU ID, an unknown WSU ID and a student who is already a member leave the list unchanged and show an error message
- [ ] Keep Spring Boot's `server.error.*` settings at their defaults, so a 403 shows no technical details (NFR-006-03)
- [ ] Check by hand: add a member, add with a blank WSU ID, remove a member, request to join and accept, and each officer-only action as a non-officer

### FR-004.2 Front End: Member management page (#57, TBD)
- [ ] `clubMembers.html` (new): the member list with a "Remove" button per member, the "Add Member" form with the WSU ID field and error message, and the pending requests with an "Accept" button
- [ ] `club.html`: the "Request to Join" button for a signed-in student who is not a member and has no pending request, and a "Manage members" link for officers only
- [ ] Insert the header from FR-006 as the first element of `<body>` in `clubMembers.html`, and use the title "Manage members - ClubHub" (FR-006-07)
- [ ] Every form uses `th:action` so the CSRF token from FR-010 is included
- [ ] `styles.css`: styles for the member list, the form and the request list
- [ ] Check by hand in a browser: each button on the page, the error message, and the page as a non-officer

### Coordination with other features
- [ ] FR-010.1 (signed-in student): there is no sign-in code in the project yet, and every action here needs the signed-in student. Merge FR-010.1 first, or stub the signed-in student until it lands
- [ ] FR-006.1 (navigation and error pages): `nav.html` and `error.html` do not exist yet. Until FR-006.1 merges, `clubMembers.html` has no header, and a 403 shows Spring Boot's default error page. Insert the header and check the 403 page once it is merged
- [ ] FR-002.8 My Club Requests (#58): the student-side view of join requests reuses `JoinRequest`, so agree on its fields and status values with that task before either merges

### Tests
- [ ] `validation.md` for this folder (TBD)
- [ ] Automated tests from `validation.md` (TBD), one class per scenario in `requirements.md`: add a member, add with a blank WSU ID, remove a member, request to join and accept, and a non-officer refused with HTTP 403. They can reuse `SampleStudents` from `StudentTests`

## Files to Create or Modify

All paths are under `code/backend/`.

- `src/main/java/com/clubhub/entities/ClubRole.java` (changed, if needed): `PRESIDENT`, `OFFICER`, `MEMBER` and `isOfficer()`
- `src/main/java/com/clubhub/entities/Member.java` (changed, if needed): keep the student-club link and its role
- `src/main/java/com/clubhub/entities/JoinRequest.java` (new): the join request
- `src/main/java/com/clubhub/entities/RequestStatus.java` (new): the request status
- `src/main/java/com/clubhub/repository/JoinRequestRepository.java` (new): join request lookups
- `src/main/java/com/clubhub/repository/MemberRepository.java` (new, if `Member` is its own entity): member lookups and delete
- `src/main/java/com/clubhub/dto/MemberForm.java` (new): the validated "Add Member" form
- `src/main/java/com/clubhub/dto/MemberResponse.java` (changed): the fields each member row needs
- `src/main/java/com/clubhub/dto/JoinRequestResponse.java` (new): the fields each request row needs
- `src/main/java/com/clubhub/controller/MemberController.java` (new): the member management routes and the officer check
- `src/main/java/com/clubhub/controller/ClubController.java` (changed): save the founder as `PRESIDENT` if it does not already, and tell `club.html` whether the signed-in student is an officer, a member or has a pending request
- `src/main/resources/templates/clubMembers.html` (new): the member management page
- `src/main/resources/templates/club.html` (changed): the "Request to Join" button and the officer-only "Manage members" link
- `src/main/resources/static/styles.css` (changed): styles for the new page
- `src/test/java/com/clubhub/MemberTests/` (new): `AddMemberTest.java`, `AddMemberValidationTest.java`, `RemoveMemberTest.java`, `JoinRequestTest.java` and `NonOfficerRefusedTest.java`

## Dependencies

- **Libraries:** none new. Spring Data JPA and Bean Validation are already used by the club and student features.
- **Features:**
  - FR-001, FR-002 and FR-003: the `Club`, `Student` and `Member` data and pages this feature builds on.
  - FR-010 User Authentication, FR-010.1: provides the signed-in student that every action here needs.
  - FR-006 Navigation and error pages: the shared header and the general error page for a refused change.
- **Work that depends on this feature:**
  - FR-002.8 My Club Requests (#58) reads the join requests created here.
  - Any later feature that needs a club's officers or members uses `Member` and `ClubRole`.
