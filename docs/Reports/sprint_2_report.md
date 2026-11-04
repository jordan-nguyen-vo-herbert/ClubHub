# Sprint 2 Report
Video Link: https://youtu.be/xUVPhOCYSmY

## What's New (User Facing)
 * Feature 1: Sign-in (FR-010)
   * FR-010.1: Back End Sign-in
   * FR-010.2: Front End Sign-in Page
 * Feature 2: Navigation and error pages (FR-006)
   * FR-006.1: Front End Navigation and Error Pages
 * Feature 3: Club registration (FR-002)
   * FR-002.6: Back End Registration Edge Cases
   * FR-002.7: Back End Signed-in Club Registration
   * FR-002.8: Front End Confirmation and My Club Requests
 * Feature 4: Club search (FR-003)
   * FR-003.3: Back End Search Refinements
 * Feature 5: Member management (FR-004)
   * FR-004.1: Back End Manage Members

## Work Summary (Developer Facing)
Sprint 2 was our first sprint using specification-driven development. Before writing code, we wrote a project constitution (mission, roadmap and tech stack) and a spec folder for each feature with its requirements, design and plan, and we turned each feature's Gherkin scenarios into validation documents that the tests were written from. We also acted on Sprint 1's retrospective: the project manager restructured the Kanban board so every item had one owner, story points and a due date from day one, changes went through pull requests, and branch protection on main required a teammate's approval before merging, which 13 of the 14 merged pull requests received. We moved sign-in up from Sprint 3, because member management and seven of Sprint 1's open scenarios depend on a signed-in user, and built it with Spring Security: a filter chain checks every request before it reaches a controller, passwords are stored as BCrypt hashes, and the passwords Sprint 1 stored as typed are hashed when the application starts. Our biggest barriers were communication and timing. The team held no meetings or check-ins this sprint, pull requests took two to five days to merge, and late back-end items held up the pages built on them, so most back-end items missed their dates. Our most significant learning was that specs made implementation faster but left us with less hands-on understanding of the code, so in Sprint 3 we will take a hybrid approach, learning the tools in tandem with an LLM while specs still define each feature.

## Unfinished Work
As of November 4, two Sprint 2 items are unfinished. The member management page (FR-004.2) was on hold until the member management back end merged on November 3, and the move to PostgreSQL (DB-1) was postponed again, because running PostgreSQL locally needs Docker volumes, which we haven't covered yet. Both move to Sprint 3. One fix is also waiting on review: pull request #81 left five search and navigation tests failing on main, and pull request #86 updates the routes and those tests so all 84 tests pass. FR-006.2 (mobile layout and search page) was dropped at planning.

## Completed Issues/User Stories
Here are links to the issues that we completed in this sprint:

 * SDD-1 Docs: Project constitution: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/44
 * SDD-2 Docs: FR-010 spec: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/45
 * SDD-3 Docs: FR-004 spec: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/46
 * SDD-4 Docs: FR-006 spec: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/47
 * SDD-5 Docs: Validation: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/48
 * FR-010.1 Back End: Sign-in: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/49
 * FR-010.2 Front End: Sign-in page: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/55
 * FR-006.1 Front End: Navigation and error pages: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/56
 * FR-002.7 Back End: Signed-in club registration: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/50
 * FR-002.6 Back End: Registration edge cases: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/52
 * FR-002.8 Front End: Confirmation and My Club Requests: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/58
 * FR-003.3 Back End: Search refinements: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/53
 * FR-004.1 Back End: Manage members: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/51
 * TEST-010 Tests: Sprint 1 fixes: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/59
 * TEST-011 Tests: Sign-in and members: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/60
 * TEST-012 Tests: Navigation, search and registration: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/61

## Incomplete Issues/User Stories
Here are links to issues we worked on but did not complete in this sprint:

 * DB-1 Data: PostgreSQL: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/54 We postponed it because running PostgreSQL locally needs Docker volumes, which we haven't covered yet.
 * FR-004.2 Front End: Member management page: https://github.com/jordan-nguyen-vo-herbert/ClubHub/issues/57 It was on hold until the member management back end (#51) merged on November 3, so it moves to Sprint 3.

## Code Files for Review
Please review the following code files, which were actively developed during this sprint, for quality:
 * [SecurityConfig.java](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/java/com/clubhub/security/SecurityConfig.java)
 * [StudentUserDetailsService.java](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/java/com/clubhub/security/StudentUserDetailsService.java)
 * [PasswordMigration.java](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/java/com/clubhub/security/PasswordMigration.java)
 * [LoginController.java](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/java/com/clubhub/controller/LoginController.java)
 * [ClubController.java](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/java/com/clubhub/controller/ClubController.java)
 * [ClubRepository.java](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/java/com/clubhub/repository/ClubRepository.java)
 * [MembershipRequest.java](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/java/com/clubhub/entities/MembershipRequest.java)
 * [login.html](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/resources/templates/login.html)
 * [nav.html](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/resources/templates/fragments/nav.html)
 * [error.html](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/resources/templates/error.html)
 * [myClubs.html](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/resources/templates/myClubs.html)
 * [clubForm.html](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/resources/templates/clubForm.html)
 * [club.html](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/main/resources/templates/club.html)
 * [SignInBackEndTest.java](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/test/java/com/clubhub/SignInTests/SignInBackEndTest.java)
 * [SignInValidationTest.java](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/test/java/com/clubhub/SignInTests/SignInValidationTest.java)
 * [SignedInClubRegistrationTest.java](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/test/java/com/clubhub/ClubTests/SignedInClubRegistrationTest.java)
 * [RegistrationEdgeCasesTest.java](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/test/java/com/clubhub/ClubTests/RegistrationEdgeCasesTest.java)
 * [NavigationValidationTest.java](https://github.com/jordan-nguyen-vo-herbert/ClubHub/blob/main/code/backend/src/test/java/com/clubhub/NavigationTests/NavigationValidationTest.java)

## Retrospective Summary
Here's what went well:
  * Item 1: Objectives, owners and deadlines were set on day one
  * Item 2: Branch protection on main: 13 of 14 merged pull requests were approved by a teammate, up from none in Sprint 1
  * Item 3: Specs before code: a project constitution and a spec for each Sprint 2 feature
  * Item 4: Sign-in, navigation and error pages, club registration with its edge cases and confirmation page, search refinements and the member management back end all shipped
  * Item 5: The two Sprint 1 club test classes that Maven skipped now run, and the test suite grew from 40 to 84 tests, which all pass with #86

Here's what we'd like to improve:
   * Item 1: Communication and availability, since the team held no meetings this sprint
   * Item 2: Reviews across specialties, since specialized developers approved code outside their own area
   * Item 3: Hands-on understanding of the code under specification-driven development
   * Item 4: Meeting deadlines, since pull requests took two to five days to merge and late back-end items blocked the pages built on them

Here are changes we plan to implement in the next sprint:
   * Item 1: Hold a weekly 30-minute meeting at a fixed time, and anyone who can't attend posts their update in the chat beforehand
   * Item 2: Rotate responsibilities every sprint, and have authors walk the reviewer through each change before approval
   * Item 3: Take a hybrid approach, learning the tools in tandem with an LLM while each owner writes their own code
   * Item 4: Open a draft pull request within three days of starting an item, review within 24 hours, schedule blocking back-end items for week one, and ship tests with each feature
