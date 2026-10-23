# Project Roadmap

ClubHub is built in three phases, one per sprint. Each phase ends with a working deliverable that the next phase builds on, and each feature lands after the features it depends on. At the start of Sprint 2 the order was revised on that basis: sign-in (FR-010) moved up to Sprint 2, and events (FR-005) moved back to Sprint 3. The client expects at least 60% of features by the end of Sprint 2, which is six of the ten.

Issue numbers refer to the GitHub issues, which hold the full user stories and Gherkin scenarios.

## Phase 1: Profiles, Club Registration and Search (Sprint 1, Sep 15 to Oct 12)
**Goal:** Set up the Spring Boot, JPA and Thymeleaf architecture and deliver the first features end to end, from submitting a form to viewing the saved result.

**Features:**
- **FR-001 User Profile Creation**
  - FR-001.1 Back End: User Profile Form (#28)
  - FR-001.2 Front End: User Profile Page (#15)
  - FR-001.3 Back End: User Profile Submit (#26)
  - FR-001.4 Front End: User Profile Form & Submission (#30)
- **FR-002 Club Registration**
  - FR-002.1 Back End: Club Registration Form (#27)
  - FR-002.2 Front End: Club Registration Form (#31)
  - FR-002.3 Back End: Club Registration Submission (#7)
  - FR-002.4 Front End: Club Page (#33)
  - FR-002.5 Front End: Club Registration Submission (#32)
- **FR-003 Club Search**
  - FR-003.1 Back End: Club Search (#9)
  - FR-003.2 Front End: Club Search (#34)
- **Tests**
  - TEST-001: Shared Test Data – 5 Students, 5 Clubs (#16)
  - TEST-002: Student Profile Creation (FR-001) (#17)
  - TEST-003: Duplicate WSU ID Error (FR-001) (#18)
  - TEST-004: Student Profile Page (FR-001) (#19)
  - TEST-005: Club Registration (FR-002) (#24)
  - TEST-006: Unknown Founder Error (FR-002) (#20)
  - TEST-007: Club Page (FR-002) (#21)
  - TEST-008: Club Search by Name (FR-003) (#22)
  - TEST-009: Club Search by Area (FR-003) (#23)

**Completion Criteria:**
- All three features work end to end, from submitting a form to viewing the saved result
- All 20 Sprint 1 items are closed (58 story points)
- Sprint 1 report, retrospective and demo video are submitted

**Status:** Complete. Sprint 1 scenarios that need sign-in, navigation or club approval carry over to Phases 2 and 3, and the move to PostgreSQL carries over to Phase 2.

## Phase 2: Sign-in, Members and Navigation (Sprint 2, Oct 18 to Nov 5)
**Goal:** Sign students in so every page knows who is using it, then add member management and site-wide navigation. This brings ClubHub to six of ten features. The phase also starts specification-driven development: each feature's spec is written and reviewed before its code.

**Features:**
- **Specification Driven Development Documents** (#25)
  - SDD-1 Docs: Project constitution (#44)
  - SDD-2 Docs: FR-010 spec (#45)
  - SDD-3 Docs: FR-004 spec (#46)
  - SDD-4 Docs: FR-006 spec (#47)
  - SDD-5 Docs: Validation (#48)
- **FR-002 Club Registration**
  - FR-002.6 Back End: Registration edge cases (#52)
  - FR-002.7 Back End: Signed-in club registration (#50), needs FR-010.1
  - FR-002.8 Front End: Confirmation and My Club Requests (#58)
- **FR-003 Club Search**
  - FR-003.3 Back End: Search refinements (#53)
- **FR-004 Manage Members** (#12)
  - FR-004.1 Back End: Manage members (#51)
  - FR-004.2 Front End: Member management page (#57)
- **FR-006 Working Webpage/User Interface** (#10)
  - FR-006.1 Front End: Navigation and error pages (#56)
- **FR-010 User Authentication** (#5)
  - FR-010.1 Back End: Sign-in (#49)
  - FR-010.2 Front End: Sign-in page (#55)
- **DB-1 Data: PostgreSQL** (#54)
- **Tests**
  - TEST-010 Tests: Sprint 1 fixes (#59)
  - TEST-011 Tests: Sign-in and members (#60)
  - TEST-012 Tests: Navigation, search and registration (#61)

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
- **FR-009 User Roles and Permissions** (#14), needs FR-010
  - FR-009.1 Back End: Roles and club approval (#62)
  - FR-009.2 Front End: Admin review page (#63)
- **FR-008 Feedback Option** (#11), needs FR-010
  - FR-008.1 Back End: Feedback (#64)
  - FR-008.2 Front End: Feedback form (#65)
- **FR-005 Event Management and Notifications** (#8), needs FR-004 and FR-009
  - FR-005.1 Back End: Events (#66)
  - FR-005.2 Back End: Email notifications (#67)
  - FR-005.3 Front End: Event form and calendar (#68)
- **FR-007 Analytics Dashboard** (#13), needs FR-004 and FR-005
  - FR-007.1 Back End: Analytics (#69)
  - FR-007.2 Front End: Analytics dashboard (#70)

**Completion Criteria:**
- An administrator can approve or reject a pending club, and the student sees the outcome on My Club Requests
- Students who are not officers are refused officer-only actions
- Signed-in users can submit feedback
- An officer can create an event, an invalid date is rejected, and every club member gets an email
- The dashboard shows member, non-member and total attendance and membership numbers, with a message when there isn't enough data
- The remaining Sprint 1 scenarios, which wait on club approval, are met
- Each feature has a spec folder and passing tests, merged through reviewed pull requests
