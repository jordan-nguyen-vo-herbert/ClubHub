# Sprint 1 Report
Video Link: https://youtu.be/fIcN1d-MOfM

## What's New (User Facing)
 * Feature 1: User profile creation (FR-001)
   * FR-001.1: Back End User Profile Form
   * FR-001.2: Front End User Profile Page
   * FR-001.3: Back End User Profile Submission
   * FR-001.4: Front End User Profile Form & Submission
 * Feature 2: Club registration (FR-002)
   * FR-002.1: Back End Club Registration Form
   * FR-002.2: Front End Club Registration Form
   * FR-002.3: Back End Club Registration Submission
   * FR-002.4: Front End Club Page
   * FR-002.5: Front End Club Registration Submission
 * Feature 3: Club search (FR-003)
   * FR-003.1: Back End Club Search
   * FR-003.2: Front End Club Search

## Work Summary (Developer Facing)
Our team developed an intuitive understanding of the project's underlying framework. This sprint was largely focused on getting the core architecture established while learning how the back end and front end interact. Our framework is divided into three layers: entities that define the data, controllers that handle requests, and Thymeleaf pages that display the results. The front end communicates with the back end through GET requests, which load a page or form, and POST requests, which submit a form to be saved. Since each feature required both a back-end and a front-end component, and the front end depended on the core entities and controllers being in place first, we prioritized the back end and switched our controllers from returning JSON to rendering Thymeleaf pages partway through the sprint. Our biggest barrier was learning Spring Boot, JPA, and Thymeleaf in parallel with implementing features, which, combined with exam schedules, pushed most of the work and testing toward the end of the sprint and led us to complete three of our four planned functional requirements. To mitigate this, we restructured our Kanban board so that each functional requirement is split into separate front-end and back-end items, giving us clearer visibility into progress. Our most significant learning was that the course emphasizes software engineering practices over implementation details, which is why we are moving to specification-driven development with clearer deadlines and pull requests in Sprint 2.

## Unfinished Work
The move from the temporary H2 database to PostgreSQL was deferred to a later sprint. The project manager reassigned the developer responsible for the database to testing, because the team agreed it was more important to first build an intuitive understanding of the core framework. Since our data access goes through Spring Data JPA, the switch is primarily a configuration change and does not require rewriting existing features.

## Completed Issues/User Stories
Here are links to the issues that we completed in this sprint:

 * URL of issue 1: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/28
 * URL of issue 2: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/15
 * URL of issue 3: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/26
 * URL of issue 4: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/30
 * URL of issue 5: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/27
 * URL of issue 6: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/31
 * URL of issue 7: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/7
 * URL of issue 8: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/33
 * URL of issue 9: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/32
 * URL of issue 10: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/9
 * URL of issue 11: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/34
 * URL of issue 12: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/17
 * URL of issue 13: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/18
 * URL of issue 14: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/19
 * URL of issue 15: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/24
 * URL of issue 16: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/22
 * URL of issue 17: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/23
 * URL of issue 18: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/21
 * URL of issue 19: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/16
 * URL of issue 20: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/20

## Incomplete Issues/User Stories
All 20 Sprint 1 issues were completed and closed, but the following user stories still have acceptance criteria scenarios that our implementation does not meet yet. Most of these scenarios depend on features scheduled for later sprints: user authentication (FR-010) and administrator roles for approving clubs (FR-009) in Sprint 3, and member management (FR-004) and the working user interface (FR-006) in Sprint 2. The remaining gaps are edge cases that our Sprint 1 review identified as unfinished. Each link leads to the issue with its full Gherkin scenarios.

 * [FR-001.1 Back End: User Profile Form](https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/28): *As a student, I need a form that can be filled out, so that I can submit a registration request and have my ClubHub profile created.*
   * Unmet scenario: Unsuccessfully open the student profile form
   * Reason: This scenario depends on user authentication (FR-010), which is planned for Sprint 3. Until students can log in, the profile form is available to anyone, so the "not logged in" path cannot be enforced.
 * [FR-002.1 Back End: Club Registration Form](https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/27): *As a student who wants to start a new club, I need the backend to serve the club registration form with the data it needs, so that I can fill out a complete and accurate registration request.*
   * Unmet scenarios: Logged-in student opens the registration form (founding officer fields are not prefilled); Unauthenticated user requests the form
   * Reason: Both scenarios assume a logged-in student, which depends on user authentication (FR-010) in Sprint 3. In the meantime, the founder identifies themselves by entering their WSU ID, which the back end uses to look up their student profile.
 * [FR-002.2 Front End: Club Registration Form](https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/31): *As a student who wants to start a new club, I need a clear, easy-to-use registration form, so that I can submit my club request without confusion or errors.*
   * Unmet scenarios: Form displays all fields (no meeting info, faculty advisor, or contact email fields; required fields are not visibly marked); Server errors are shown inline (duplicate club name); Form works on mobile
   * Reason: For Sprint 1, we limited the club data model to the fields needed to register and display a club, in line with our goal of delivering basic student and club profiles. Mobile layout and the remaining form refinements fall under the working user interface (FR-006) in Sprint 2, and duplicate club name checks are one of the club creation edge cases our Sprint 1 review identified as unfinished.
 * [FR-002.3 Back End: Club Registration Submission](https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/7): *As a student who wants to start a new club, I need the backend to validate and store my registration submission, so that my request is saved and queued for admin approval.*
   * Unmet scenarios: Successful club registration (no notice that an administrator will review the submission); Valid submission is saved (no submission timestamp is recorded); Duplicate club name; Invalid category (no error message is shown); Unauthenticated submission
   * Reason: Registered clubs are saved with a Pending status, which the founder can see on the club page, but there is no administrator review yet. The review process depends on administrator roles (FR-009) in Sprint 3, and unauthenticated submissions depend on user authentication (FR-010). Duplicate club names, invalid category messages, and the submission timestamp are edge cases we deferred after the Sprint 1 review.
 * [FR-002.4 Front End: Club Page](https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/33): *As a student, I need a page for each club showing its information, so that I can learn about the club and decide whether to join.*
   * Unmet scenarios: Viewing an approved club; Pending club hidden from other students; Submitter can view their pending club
   * Reason: Every club currently stays Pending, because approving clubs depends on administrator roles (FR-009) in Sprint 3. The "Request to Join" button belongs to member management (FR-004) in Sprint 2, and showing a pending club only to the student who submitted it depends on user authentication (FR-010).
 * [FR-002.5 Front End: Club Registration Submission](https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/32): *As a student who submitted a club registration, I need confirmation of my submission and a way to check its status, so that I know my request went through and whether it was approved.*
   * Unmet scenarios: Confirmation after submitting (no success message or "My Club Requests" link); Viewing request status; Approved request links to club page; Rejected request shows reason; No requests yet
   * Reason: After submitting, students are redirected to the new club's page, which shows the details they entered and the club's Pending status. A success message, a request status page, and approval or rejection outcomes all depend on the administrator approval process (FR-009) in Sprint 3, and a "My Club Requests" page depends on knowing which student is logged in (FR-010).
 * [FR-003.1 Back End: Club Search](https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/9): *As a student, I need the backend to search clubs by name, category, and keywords, so that I can quickly find clubs that match my interests.*
   * Unmet scenarios: Keyword matches description; Keyword and category combined; Pending clubs excluded; No parameters
   * Reason: Sprint 1 search matches club names and filters by area of interest as two separate searches. Searching descriptions, combining a keyword with a category, and listing all clubs when no filters are given were not implemented in Sprint 1. Excluding pending clubs depends on the approval process (FR-009), since every club is currently pending.
 * [FR-003.2 Front End: Club Search](https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/34): *As a student, I need a search bar and filters on the club list page, so that I can browse and find clubs easily.*
   * Unmet scenarios: Searching shows matching clubs (results do not include a description snippet); No results (no "Clear filters" option); Paginated results
   * Reason: The search page shows matching clubs with their names and areas of interest, and keeps the search term and selected area after each search. Description snippets, a "Clear filters" option, and paginated results are refinements to the search page that fall under the working user interface (FR-006) in Sprint 2.

## Code Files for Review
Please review the following code files, which were actively developed during this sprint, for quality:
 * [StudentController.java](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/java/com/clubhub/controller/StudentController.java)
 * [ClubController.java](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/java/com/clubhub/controller/ClubController.java)
 * [Student.java](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/java/com/clubhub/entities/Student.java)
 * [Club.java](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/java/com/clubhub/entities/Club.java)
 * [ClubRepository.java](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/java/com/clubhub/repository/ClubRepository.java)
 * [studentForm.html](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/resources/templates/studentForm.html)
 * [clubForm.html](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/resources/templates/clubForm.html)
 * [clubs.html](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/resources/templates/clubs.html)
 * [StudentProfileCreationTest.java](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/test/java/com/clubhub/StudentTests/StudentProfileCreationTest.java)
 * [ClubRegistrationTest.java](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/test/java/com/clubhub/ClubTests/ClubRegistrationTest.java)

## Retrospective Summary
Here's what went well:
  * Item 1: Establishing the core software architecture and developing an intuitive understanding of how each component of our technology stack works
  * Item 2: All three features work end to end, from submitting a form to viewing the saved result
  * Item 3: Restructuring the Kanban board into front-end and back-end items gave clearer visibility into progress
  * Item 4: Front-end work was merged through pull requests, giving us a model for Sprint 2

Here's what we'd like to improve:
   * Item 1: Development Approach
   * Item 2: Clearly Defined Deadlines
   * Item 3: Stronger Communication
   * Item 4: Pull requests before merging into main
   * Item 5: Breaking Down Functional Requirements

Here are changes we plan to implement in the next sprint:
   * Item 1: Switch to specification-driven development, starting with the project constitution
   * Item 2: Set concrete deadlines for each task, with the project manager assigning work and following up
   * Item 3: Meet more often so the team stays in sync and testing happens alongside development
   * Item 4: Open a pull request for every change instead of committing directly to main
   * Item 5: Split each functional requirement into separate front-end and back-end Kanban items
