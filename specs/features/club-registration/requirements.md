# Feature Requirements

Signed-in club registration (FR-002 Club Registration), on the board as FR-002.7 Back End: Signed-in club registration (#50). Now that students sign in (FR-010.1, #49), the founder of a new club is the signed-in student instead of a typed WSU ID. This feature also carries over the open Sprint 1 scenarios from #27, #7 and #33 that needed sign-in, and adds the back end for FR-002.8 Front End: Confirmation and My Club Requests (#58).

## User Story: US-02

**As a** student who wants to start a new club,  
**I want** to register it under my own profile,  
**so that** my request is saved and queued for approval with me as its founder.

## User Story: US-02.7 (FR-002.7 Back End: Signed-in club registration, #50)

**As a** signed-in student,  
**I want** the registration form to know who I am,  
**so that** I don't have to type my WSU ID and nobody can register a club in my name.

## User Story: US-02.8 (FR-002.8 Front End: Confirmation and My Club Requests, #58)

**As a** student who submitted a club registration,  
**I want** confirmation of my submission and a list of my requests,  
**so that** I know my request went through and can check its status.

## Functional Requirement: FR-002-01
The system shall make the signed-in student the founder of every club they register. The registration form shall no longer ask for a WSU ID. (FR-002.7)

## Functional Requirement: FR-002-02
The registration form shall show the signed-in student's name and email as the founding officer, and the student shall not be able to change them on the form. (FR-002.7)

## Functional Requirement: FR-002-03
The system shall save a new club with the Pending status, and with its founder as its first member in the President role. (FR-002.7)

## Functional Requirement: FR-002-04
A signed-out visitor who opens the registration form or submits a registration shall be redirected to the sign-in page, and nothing shall be saved. (FR-002.7, through FR-010-05)

## Functional Requirement: FR-002-05
After a successful registration, the system shall show the new club's page with a message that the registration was submitted and is waiting for approval, and a link to My Club Requests. Refreshing that page shall not register the club again. (FR-002.7, FR-002.8)

## Functional Requirement: FR-002-06
The club page shall show the club's status, so the founder of a pending club sees that it is Pending. (FR-002.7)

## Functional Requirement: FR-002-07
My Club Requests (`/clubs/mine`) shall list every club the signed-in student founded, newest first, with each club's name, its status and a link to its page. With no clubs, it shall say there are no requests yet and link to the registration form. (FR-002.7, FR-002.8)

## Non-Functional Requirement: NFR-002-01
The founder shall come only from the signed-in session on the server. The system shall ignore any founder or WSU ID sent with the form, so a student can't register a club in someone else's name.

## Non-Functional Requirement: NFR-002-02
My Club Requests shall show only the signed-in student's own clubs.

## Acceptance Criteria

### FR-002.7 Back End: Signed-in club registration (#50)

The first four scenarios are the ones on #50, carried over from #27, #7 and #33.

```gherkin
Feature: Back End: Signed-in club registration

Scenario: Logged-in student opens the registration form
Given the student is signed in
When they open the club registration form
Then the founding officer fields are filled in from their profile
```

```gherkin
Scenario: Unauthenticated user requests the form
Given the user is not signed in
When they open the club registration form
Then they are redirected to the sign-in page
```

```gherkin
Scenario: Unauthenticated submission
Given the user is not signed in
When a club registration is submitted
Then it is not saved
And they are redirected to the sign-in page
```

```gherkin
Scenario: Submitter can view their pending club
Given a student registered a club that is still pending
When they open that club's page
Then they see the club with its Pending status
```

```gherkin
Scenario: The founder is the signed-in student
Given the student is signed in
When they register a club, and the request also carries another student's WSU ID
Then the club is saved with the signed-in student as its founder
And the signed-in student is its President
```

```gherkin
Scenario: Registration is confirmed
Given the student is signed in
When they register a club with valid details
Then they are taken to the new club's page
And the page says the registration was submitted and links to My Club Requests
```

```gherkin
Scenario: My Club Requests lists only the student's own clubs
Given the student has registered two clubs
And another student has registered one club
When the student opens My Club Requests
Then they see their two clubs, newest first, each with its status
And they do not see the other student's club
```

### FR-002.8 Front End: Confirmation and My Club Requests (#58)

```gherkin
Feature: Front End: Confirmation and My Club Requests

Scenario: Confirmation after submitting
Given a signed-in student submits a valid club registration
Then they see a success message
And a link to My Club Requests
```

```gherkin
Scenario: No requests yet
Given a signed-in student has not registered a club
When they open My Club Requests
Then they see that there are no requests yet
And a link to register a club
```
