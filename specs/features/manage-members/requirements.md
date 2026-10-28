# Feature Requirements

Manage members (FR-004, #12). A club officer keeps the club's member list accurate by adding and removing members by WSU ID, and handles the requests students send to join. A student who isn't an officer can't change the list.

## User Story: US-TBD (FR-004.1 Back End: Manage members, #12)

**As a** club officer,  
**I need** to manage my club's members,  
**so that** I can keep the member list accurate and handle membership requests.

### Details and Assumptions
- Needs FR-010.1 for the signed-in student.
- A club's officers are its members with an officer role; the founder is saved as President.
- Members are added by WSU ID.

## Functional Requirement: FR-004-01
On the club member management page, an officer shall be able to add a student to the club by entering the student's WSU ID and clicking "Add Member". The student shall then appear in the club's member list.

## Functional Requirement: FR-004-02
When an officer clicks "Add Member" with the required member information (the WSU ID) left blank, the system shall show an error message and shall not add a member to the member list.

## Functional Requirement: FR-004-03
An officer shall be able to remove a member from the club. The removed student shall no longer appear in the club's member list.

## Functional Requirement: FR-004-04
A signed-in student who is not a member of a club shall be able to click "Request to Join" on the club page. The club's officers shall then see the request, and an officer shall be able to accept it, which adds the student as a member.

## Functional Requirement: FR-004-05
Only a club's officers shall be able to add or remove its members. A club's officers are its members with an officer role, and the founder is saved as President. When a signed-in student who is not an officer of the club tries to add or remove a member, the system shall refuse the change.

## Non-Functional Requirement: NFR-004-01
Who counts as an officer shall be decided by one check on the server, run before every action that changes the member list or accepts a request. Hiding a button is not enough to protect an action.

## Non-Functional Requirement: NFR-004-02
Every action in this feature shall be done by the signed-in student (FR-010.1). The system shall take the acting student from the sign-in, never from a form field or URL.

## Non-Functional Requirement: NFR-004-03
An automated test shall cover each scenario below.

## Non-Functional Requirement: NFR-004-04
Pages shall display correctly in current desktop versions of Chrome, Edge and Firefox at a window width of 1024 pixels or more. A mobile layout is out of scope (`mission.md`).

## Acceptance Criteria

### FR-004.1 Back End: Manage members (#12)

```gherkin
Feature: Back End: Manage members

Scenario: Successfully add a club member
Given I am a club officer
And I am on the club member management page
When I enter a student's required information
And I click the "Add Member" button
Then the student is added to the club's member list
```

```gherkin
Scenario: Add a member without required information
Given I am a club officer
And I am on the club member management page
When I leave the required member information blank
And I click the "Add Member" button
Then I see an error message
And the member is not added to the member list
```

```gherkin
Scenario: Remove a member
Given I am a club officer
When I remove a member
Then they no longer appear in the club's member list
```

```gherkin
Scenario: Student requests to join
Given a signed-in student who is not a member of the club
When they click "Request to Join" on the club page
Then the club's officers see the request
And an officer can accept it, which adds the student as a member
```

```gherkin
Scenario: Non-officer cannot manage members
Given a signed-in student who is not an officer of the club
When they try to add or remove a member
Then the change is refused
```
