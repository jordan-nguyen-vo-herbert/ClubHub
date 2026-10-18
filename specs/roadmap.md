# Project Roadmap

ClubHub is built in three phases, one per sprint. Each phase ends with a working deliverable that the next phase builds on, and each feature lands after the features it depends on. At the start of Sprint 2 the order was revised on that basis: sign-in (FR-010) moved up to Sprint 2, and events (FR-005) moved back to Sprint 3. The client expects at least 60% of features by the end of Sprint 2, which is six of the ten.

Issue numbers refer to the GitHub issues, which hold the full user stories and Gherkin scenarios.

## Phase 1: Profiles, Club Registration and Search (Sprint 1, Sep 15 to Oct 12)
**Goal:** Set up the Spring Boot, JPA and Thymeleaf architecture and deliver the first features end to end, from submitting a form to viewing the saved result.

**Features:**
- FR-001 User profile creation: profile form, submission and profile page (#28, #15, #26, #30)
- FR-002 Club registration (US-02): registration form, submission and club page (#27, #31, #7, #33, #32)
- FR-003 Club search: search by name and by area of interest (#9, #34)
- Tests TEST-001 to TEST-009 for the three features

**Completion Criteria:**
- All three features work end to end, from submitting a form to viewing the saved result
- All 20 Sprint 1 items are closed (58 story points)
- Sprint 1 report, retrospective and demo video are submitted

**Status:** Complete. Sprint 1 scenarios that need sign-in, navigation or club approval carry over to Phases 2 and 3, and the move to PostgreSQL carries over to Phase 2.

## Phase 2: Sign-in, Members and Navigation (Sprint 2, Oct 18 to Nov 5)
**Goal:** Sign students in so every page knows who is using it, then add member management and site-wide navigation. This brings ClubHub to six of ten features. The phase also starts specification-driven development: each feature's spec is written and reviewed before its code.

**Features:**
- Project constitution and feature specs (SDD-1 to SDD-5, #44 to #48): `specs/mission.md`, `roadmap.md` and `tech-stack.md`, plus spec folders for sign-in, manage-members and navigation
- FR-010 User authentication (US-01, #5): sign-in with the WSU email and password from the student's profile, passwords stored as BCrypt hashes, sign-out, and a redirect to sign-in on pages that need it (#49, #55)
- FR-004 Manage members (#12): officers add members by WSU ID and remove them, and students request to join (#51, #57)
- FR-006 Working web interface (#10): navigation on every page and an unavailable-page screen with a link home (#56)
- FR-002 and FR-003 follow-ups: signed-in club registration (#50), registration edge cases (#52), a confirmation and My Club Requests page (#58), and search refinements (#53)
- DB-1 (#54): move from H2 to PostgreSQL, keeping H2 for tests
- Tests TEST-010 to TEST-012 (#59 to #61)

**Completion Criteria:**
- The constitution and the three feature spec folders are merged to `main` through reviewed pull requests by Oct 24
- A student signs in with their WSU email and password, no password is stored as typed, and pages that need a signed-in student redirect to sign-in
- Club registration takes the founder from the signed-in student
- Officers can add and remove members, and students can request to join
- Every page has navigation, and an unavailable page links back home
- Data survives a restart on PostgreSQL, and the tests still run on H2
- `./mvnw test` runs every test class, and all of them pass
- All code is merged by Oct 29, and the report, retrospective, slides and video are submitted by Nov 5

## Phase 3: Roles, Feedback, Events and Analytics (Sprint 3, dates set at Sprint 3 planning)
**Goal:** Finish the remaining four features in dependency order: roles and feedback first, then events, then analytics. This completes a club management system with its administrative side for the final demo.

**Features:**
- FR-009 User roles and permissions (#14): an administrator account, approving or rejecting pending clubs with a reason, officer roles with officer-only checks, and an admin review page (#62, #63). Needs FR-010.
- FR-008 Feedback (#11): signed-in users submit feedback through a form with required fields (#64, #65). Needs FR-010.
- FR-005 Event management and notifications (US-03, #8): officers create events with a valid date, time and location, every club member is emailed, and events appear on a schedule and calendar (#66 to #68). Needs FR-004 and FR-009.
- FR-007 Analytics dashboard (#13): membership and attendance counts per club on a dashboard (#69, #70). Needs FR-004 and FR-005.

**Completion Criteria:**
- An administrator can approve or reject a pending club, and the student sees the outcome on My Club Requests
- Students who are not officers are refused officer-only actions
- Signed-in users can submit feedback
- An officer can create an event, an invalid date is rejected, and every club member gets an email
- The dashboard shows member, non-member and total attendance and membership numbers, with a message when there isn't enough data
- The remaining Sprint 1 scenarios, which wait on club approval, are met
- Each feature has a spec folder and passing tests, merged through reviewed pull requests
