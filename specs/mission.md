# Project Mission

## Problem
Students who want to find a club at WSU, start one, or run one have no single place to do it. Finding a club, requesting to register a new one, keeping a member list, announcing events and getting a club approved all happen in different places. As a result, students miss clubs that match their interests, officers spend their time on bookkeeping, and administrators have no single view of the clubs they oversee. ClubHub brings this work into one web application backed by one database.

## Target Users
- **Students:** create a profile, search for clubs, request to join them, and attend their events.
- **Club founders and officers:** register a club, manage its members, schedule events, and track attendance.
- **University administrators:** approve or reject club registrations and assign officer roles.

Every signed-in user can also send feedback, which the ClubHub team reviews.

## Project Goals
- Deliver all ten features by the final demo, with at least six of the ten (60%) working at the end of Sprint 2.
- End every sprint with a working deliverable that the next sprint builds on.
- Protect student and club data: passwords are stored as hashes, and role checks are enforced on the server, not only hidden in the page.
- Keep the interface clean and easy to use, so every page is reachable from the navigation and errors explain what to fix.
- Write each feature's spec before its code, and merge it only with passing tests through a reviewed pull request.
- Make sure every team member understands Spring Boot, JPA and Thymeleaf well enough to explain and change any code they ship, including code generated with AI tools.

## In Scope
Each requirement below matches the Kanban board, split into its back-end and front-end items.

- **FR-001 User Profile Creation** (Sprint 1)
  - FR-001.1 Back End: User Profile Form
  - FR-001.2 Front End: User Profile Page
  - FR-001.3 Back End: User Profile Submit
  - FR-001.4 Front End: User Profile Form & Submission
- **FR-002 Club Registration** (Sprints 1 and 2)
  - FR-002.1 Back End: Club Registration Form
  - FR-002.2 Front End: Club Registration Form
  - FR-002.3 Back End: Club Registration Submission
  - FR-002.4 Front End: Club Page
  - FR-002.5 Front End: Club Registration Submission
  - FR-002.6 Back End: Registration edge cases (Sprint 2)
  - FR-002.7 Back End: Signed-in club registration (Sprint 2)
  - FR-002.8 Front End: Confirmation and My Club Requests (Sprint 2)
- **FR-003 Club Search** (Sprints 1 and 2)
  - FR-003.1 Back End: Club Search
  - FR-003.2 Front End: Club Search
  - FR-003.3 Back End: Search refinements (Sprint 2)
- **FR-004 Manage Members** (Sprint 2)
  - FR-004.1 Back End: Manage members
  - FR-004.2 Front End: Member management page
- **FR-005 Event Management and Notifications** (Sprint 3)
  - FR-005.1 Back End: Events
  - FR-005.2 Back End: Email notifications
  - FR-005.3 Front End: Event form and calendar
- **FR-006 Working Webpage/User Interface** (Sprint 2)
  - FR-006.1 Front End: Navigation and error pages
- **FR-007 Analytics Dashboard** (Sprint 3)
  - FR-007.1 Back End: Analytics
  - FR-007.2 Front End: Analytics dashboard
- **FR-008 Feedback Option** (Sprint 3)
  - FR-008.1 Back End: Feedback
  - FR-008.2 Front End: Feedback form
- **FR-009 User Roles and Permissions** (Sprint 3)
  - FR-009.1 Back End: Roles and club approval
  - FR-009.2 Front End: Admin review page
- **FR-010 User Authentication** (Sprint 2)
  - FR-010.1 Back End: Sign-in
  - FR-010.2 Front End: Sign-in page
- **DB-1 Data: PostgreSQL** (Sprint 2)

The board also tracks the spec documents (SDD-1 to SDD-5) and automated tests (TEST-001 to TEST-012) for these requirements.

## Out of Scope
- **Real WSU single sign-on:** students sign in with a password stored in ClubHub instead of their WSU account.
- **Other universities:** ClubHub serves WSU only. Support for more universities is a possible future expansion.
- **A mobile layout:** ClubHub is designed for desktop browsers.
- **Ideas from the Sept 20 brainstorm that never became requirements:** event reminders, "suggested for you" events, RSVPs, officer transitions and club deactivation.
