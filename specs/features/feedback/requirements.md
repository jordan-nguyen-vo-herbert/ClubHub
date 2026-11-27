# Feature Requirements

Feedback (FR-008 Feedback Option, #11), split on the board into FR-008.1 Back End: Feedback (#64) and FR-008.2 Front End: Feedback form (#65). A signed-in user sends the ClubHub team a bug report, a suggestion or another comment, and the team reviews it later. FR-008.1 stores the feedback and checks it on the server, and ships a plain feedback page wired to the fixed names in `design.md`, so FR-008.2 only has to style it.

## User Story: US-06

**As a** ClubHub user,  
**I need** the option to submit feedback about ClubHub,  
**so that** I can report issues and potential suggestions.

### Details and Assumptions
- The user must be registered and signed in (FR-010).
- A feedback form is available within ClubHub.
- Submitted feedback is stored for review by the developers.

## User Story: US-06.1 (FR-008.1 Back End: Feedback, #64)

**As a** ClubHub user,  
**I need** the option to submit feedback about ClubHub,  
**so that** I can report issues and potential suggestions.

## User Story: US-06.2 (FR-008.2 Front End: Feedback form, #65)

**As a** ClubHub user,  
**I need** a feedback form and a confirmation,  
**so that** I know my feedback was received.

## Functional Requirement: FR-008-01
A signed-in user shall be able to open the feedback form at `/feedback`. The form shall have two required fields: a category (Bug report, Suggestion or Other) and a message. (FR-008.1)

## Functional Requirement: FR-008-02
When a signed-in user submits the form with both fields filled in, the system shall store the feedback with its category, its message, the user who sent it and the time it was sent. (FR-008.1)

## Functional Requirement: FR-008-03
After storing the feedback, the system shall show the feedback page again with the confirmation "Thanks, your feedback was received." and an empty form. Refreshing the page shall not send the feedback again. (FR-008.1, FR-008.2)

## Functional Requirement: FR-008-04
When the category is not chosen, or the message is empty or only spaces, the system shall not store the feedback. It shall show the form again with a message next to each field that needs completing, and keep what the user typed. (FR-008.1)

## Functional Requirement: FR-008-05
When the message is longer than 1000 characters, the system shall not store the feedback, and shall show "Keep your feedback to 1000 characters or fewer" next to the message field. (FR-008.1)

## Functional Requirement: FR-008-06
When the category is not one of the listed categories, the system shall not store the feedback, and shall show "Choose a category from the list" next to the category field. (FR-008.1)

## Functional Requirement: FR-008-07
A signed-out visitor who opens or submits the feedback form shall be sent to the sign-in page (FR-010-05), and nothing shall be stored. (FR-008.1)

## Functional Requirement: FR-008-08
The feedback page shall show the navigation header (FR-006-01), the category and message fields with the required ones marked, and the confirmation from FR-008-03. (FR-008.2)

## Non-Functional Requirement: NFR-008-01
The system shall take the user who sent the feedback from the sign-in, never from a form field or URL, so nobody can send feedback in another student's name.

## Non-Functional Requirement: NFR-008-02
Every check in FR-008-04 to FR-008-06 shall run on the server. The browser's `required` and `maxlength` attributes help the user, but don't protect the data.

## Non-Functional Requirement: NFR-008-03
An error message on the feedback form shall never show a Java class name or other technical detail (NFR-006-03).

## Non-Functional Requirement: NFR-008-04
Stored feedback shall not be shown on any page in this feature. Developers review it in the database. A page for reviewing feedback would need the administrator role from FR-009.

## Non-Functional Requirement: NFR-008-05
An automated test shall cover each FR-008.1 scenario below.

## Acceptance Criteria

### FR-008.1 Back End: Feedback (#64)

#### From the user story (#64)

```gherkin
Feature: Back End: Feedback

Scenario: Successfully submit feedback
Given the user is registered and logged in to ClubHub
And the feedback form is available
When the user completes and submits the feedback form
Then ClubHub confirms that the feedback was successfully received
And the feedback data is stored for review by the developers
```

```gherkin
Scenario: Submit an empty feedback form
Given the user is on the feedback form page
When the user leaves the required fields empty
And attempts to submit the feedback form
Then ClubHub does not allow the submission
And the user is prompted to complete the required fields
```

#### Extra scenarios

These four are not part of the original user story. They were added so that each functional and non-functional requirement above has a scenario to test against.

```gherkin
Scenario: Message is too long
Given the user is signed in and on the feedback form page
When they submit a message longer than 1000 characters
Then the feedback is not stored
And they are told to keep their feedback to 1000 characters or fewer
And what they typed stays in the form
```

```gherkin
Scenario: Category is not in the list
Given the user is signed in
When they submit feedback with a category that is not Bug report, Suggestion or Other
Then the feedback is not stored
And they are told to choose a category from the list
```

```gherkin
Scenario: Feedback is stored under the signed-in user
Given the user is signed in
When they submit feedback with an extra field naming another student
Then the feedback is stored with the signed-in user as its sender
```

```gherkin
Scenario: Signed-out visitor opens the feedback form
Given a visitor is not signed in
When they open or submit the feedback form
Then they are sent to the sign-in page
And no feedback is stored
```

### FR-008.2 Front End: Feedback form (#65)

```gherkin
Feature: Front End: Feedback form

Scenario: Feedback form shows its fields
Given the user is signed in
When they open the feedback page
Then they see the feedback fields with the required ones marked
```

```gherkin
Scenario: Confirmation after submitting
Given the user submits valid feedback
Then they see a confirmation message
```
