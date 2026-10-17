# Sprint 1 Report
Video Link: 

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

## Incomplete Issues/User Stories
FR-002.3 Back End: Club Registration Submission
Scenario: Successful club registration
  Given I am logged in with a valid student account
  And I am on the club registration page
  When I submit a registration form with a club name, description, and category
  Then the registration is saved with a status of "Pending Approval"
  And I am notified that an administrator will review my submission
We haven't implemented the status or administrator feature yet. They are for the second sprint. So after registering the president can't see the status of the club for now.

FR-002.4 Front End: Club Page
Scenario: Viewing an approved club
  Given "Chess Club" has status "APPROVED"
  When a student navigates to the Chess Club page
  Then the page shows the club's name, description, category, meeting info, advisor, contact email, and officers
  And a "Request to Join" button is shown
Status feature hasn't been implemented yet and the "Request to Join" button is also a task for Sprint 2.

FR-002.5 Front End: Club Registration Submission
Scenario: Confirmation after submitting
  Given a student successfully submits a registration for "Chess Club"
  When the confirmation page loads
  Then a success message says the request is awaiting admin approval
  And a summary of the submitted details is shown
  And a link to "My Club Requests" is shown
We are yet to implement the request and admin feature. So right now the student can only see the confirmation page and the summary.

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