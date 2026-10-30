# Implementation Plan

## Tasks

### FR-010.1 Back End: Sign-in (#49, Jordan)
- [x] Add Spring Security, the Thymeleaf Spring Security extras and `spring-security-test` to `pom.xml`
- [x] Keep `./mvnw test` passing: add `.with(csrf())` to every POST in the tests, and sign in a test user for pages that now need sign-in
- [x] `Student`: make `email` unique and required
- [x] `StudentRepository`: add `findByEmail(String email)` returning `Optional<Student>`
- [x] `SecurityConfig`: public pages (FR-010-05), sign-in at `/login` with the `email` and `password` fields, failed sign-in to `/login?error` keeping the typed email, sign-out to `/`, and a BCrypt `PasswordEncoder` bean
- [x] `StudentPrincipal` and `StudentUserDetailsService`: find the student by lowercase email, and expose their first name, student ID and `ROLE_STUDENT`
- [x] `LoginController`: `GET /login` shows `login.html` and adds the typed email as the model attribute `email` after a failed sign-in
- [x] `login.html`: a plain page wired to the fixed names (fields, error message, refilled email, links), so the sign-in flow works end to end
- [x] `StudentController`: store the email in lowercase, require `@wsu.edu`, reject a duplicate email, hash the password before saving, and sign the new student in
- [x] `StudentController`: make `/profile` use the signed-in student, and remove the `studentId` session workaround
- [x] `PasswordMigration`: at startup, hash every stored password that isn't already a BCrypt hash
- [x] White-box tests for each back-end scenario (`SignInBackEndTest`)

### FR-010.2 Front End: Sign-in page (#55, Yohann)
- [x] `login.html`: style the plain page from FR-010.1, keeping its field names, message and links
- [x] Shared header: the first name and a sign-out button when signed in, and a sign-in link when signed out (coordinate with FR-006.1, #56)

### Tests
- [ ] `validation.md` for this folder (SDD-5, #48, Daniel)
- [ ] Automated tests from `validation.md` (TEST-011, #60, Daniel)

## Files to Create or Modify

All paths are under `code/backend/`.

- `pom.xml`: add the three dependencies
- `src/main/java/com/clubhub/security/SecurityConfig.java` (new): sign-in rules and the password encoder
- `src/main/java/com/clubhub/security/StudentUserDetailsService.java` (new): finds a student by email for Spring Security
- `src/main/java/com/clubhub/security/StudentPrincipal.java` (new): the signed-in student, with their first name and role
- `src/main/java/com/clubhub/security/PasswordMigration.java` (new): hashes old plain-text passwords at startup
- `src/main/java/com/clubhub/controller/LoginController.java` (new): shows the sign-in page
- `src/main/java/com/clubhub/controller/StudentController.java`: email checks, password hashing, signing in after creation, and `/profile`
- `src/main/java/com/clubhub/repository/StudentRepository.java`: `findByEmail`
- `src/main/java/com/clubhub/entities/Student.java`: unique, required `email`
- `src/main/resources/templates/login.html` (new): the sign-in page, a plain version from FR-010.1 that Yohann styles in FR-010.2
- `src/main/resources/templates/studentForm.html`: show the email errors (`@wsu.edu`, duplicate email)
- `src/test/java/com/clubhub/SignInTests/SignInBackEndTest.java` (new): white-box tests for the back end
- `src/main/resources/templates/index.html` and the shared header (Yohann): name, sign-out button and sign-in link
- `src/main/resources/templates/no-profile.html`: no longer reachable once `/profile` needs sign-in, so it can be removed
- `src/test/java/com/clubhub/StudentTests/SampleStudents.java` and the club tests: `.with(csrf())` and a signed-in test user

## Dependencies

- **Libraries** (versions set by the Spring Boot 3.2.0 parent):
  - `org.springframework.boot:spring-boot-starter-security` (Spring Security 6.2.0)
  - `org.thymeleaf.extras:thymeleaf-extras-springsecurity6` (3.1.2)
  - `org.springframework.security:spring-security-test` (test scope)
- **Features:**
  - FR-001 User Profile Creation: sign-in uses the email and password from the profile.
  - DB-1 Data: PostgreSQL (#54): once data survives restarts, `PasswordMigration` hashes any plain-text passwords already stored.
  - FR-006.1 (#56): the shared navigation that the header changes go into.
- **Work that depends on this feature:**
  - FR-002.7 Signed-in club registration (#50) takes the founder from the signed-in student.
  - FR-009 User Roles and Permissions (#14) builds its roles on `ROLE_STUDENT`.
