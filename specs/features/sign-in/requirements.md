# Feature Requirements

Sign-in (FR-010 User Authentication, #5), split on the board into FR-010.1 Back End: Sign-in (#49) and FR-010.2 Front End: Sign-in page (#55). Real WSU single sign-on is out of scope, so students sign in with the WSU email and password stored in their ClubHub profile.

## User Story: US-01

**As a** first-time user,  
**I want** to access ClubHub with my WSU email and password,  
**so that** I can start looking at student organizations.

## User Story: US-01.1 (FR-010.1 Back End: Sign-in, #49)

**As a** student,  
**I want** ClubHub to check my WSU email and password,  
**so that** only I can act under my profile.

## User Story: US-01.2 (FR-010.2 Front End: Sign-in page, #55)

**As a** student,  
**I want** a sign-in page and a way to sign out,  
**so that** I can start and end my session.

## Functional Requirement: FR-010-01
The system shall let a student sign in with the WSU email and password from their profile. (FR-010.1)

## Functional Requirement: FR-010-02
The system shall store every password as a BCrypt hash: when a profile is created, and once at startup for any stored password that is not already a BCrypt hash. (FR-010.1)

## Functional Requirement: FR-010-03
The system shall require each profile to have a unique email that ends in `@wsu.edu`, store it in lowercase, and reject a duplicate with "A profile already exists for this email". (FR-010.1)

## Functional Requirement: FR-010-04
The system shall sign a student in automatically after they create a profile, and show their new profile page. (FR-010.1)

## Functional Requirement: FR-010-05
The system shall allow only these pages without sign-in: home (`/`), sign-in (`/login`), create a profile (`/studentForm`), styles (`/styles.css`) and error pages (`/error`). Every other page shall redirect a signed-out visitor to the sign-in page. (FR-010.1)

## Functional Requirement: FR-010-06
After a student signs in, the system shall return them to the page they were trying to open, or to the home page if there was none. (FR-010.1)

## Functional Requirement: FR-010-07
The system shall reject a wrong email or password with "A valid school account is required", show the sign-in page again, and keep the email the student typed in its field. (FR-010.1, FR-010.2)

## Functional Requirement: FR-010-08
The system shall end the session when a student signs out and return them to the home page. (FR-010.1, FR-010.2)

## Functional Requirement: FR-010-09
"My student profile" (`/profile`) shall open the signed-in student's own profile. (FR-010.1)

## Functional Requirement: FR-010-10
The sign-in page shall show WSU email and password fields, a sign-in button, and links to create a student profile and to return home. (FR-010.2)

## Functional Requirement: FR-010-11
The header of every page shall show the signed-in student's first name and a sign-out button, and show a sign-in link to a signed-out visitor. (FR-010.2)

## Non-Functional Requirement: NFR-010-01
The system shall never include a password or password hash in a page, a log message or a JSON response.

## Non-Functional Requirement: NFR-010-02
The system shall show the same message for a failed sign-in whether the email or the password was wrong, so it never reveals which emails have a profile.

## Non-Functional Requirement: NFR-010-03
Every form that sends a POST shall include a CSRF token, and the system shall reject a POST without a valid token with HTTP 403.

## Non-Functional Requirement: NFR-010-04
The session ID shall travel only in a cookie, never in a URL, and a session shall end after 30 minutes without activity.

## Non-Functional Requirement: NFR-010-05
Checking a password at sign-in shall take under one second on a team member's laptop (BCrypt strength 10).

## Acceptance Criteria

### FR-010.1 Back End: Sign-in (#49)

```gherkin
Feature: Back End: Sign-in

Scenario: Successful sign-in
Given a student has a ClubHub profile
When they sign in with their WSU email and password
Then they are signed in
And they are redirected to the ClubHub home page
```

```gherkin
Scenario: Sign-in with invalid credentials
Given the student has opened the sign-in page
When they enter an email or password that does not match a profile
Then access is denied
And they are told that a valid school account is required
```

```gherkin
Scenario: Signed-out student opens a page that needs sign-in
Given the student is not signed in
When they open a page that needs a signed-in student
Then they are redirected to the sign-in page
```

```gherkin
Scenario: Sign-in returns to the page the student wanted
Given the student is not signed in
And they opened a page that needs sign-in and were sent to the sign-in page
When they sign in with their WSU email and password
Then they are taken to the page they first opened
```

```gherkin
Scenario: Passwords are stored hashed
Given a student creates a profile with a password
When the profile is saved
Then the stored password is a BCrypt hash, not the typed password
```

```gherkin
Scenario: Existing plain-text passwords are hashed at startup
Given a stored profile has a password that is not a BCrypt hash
When ClubHub starts
Then that password is replaced with its BCrypt hash
And the student can still sign in with their original password
```

```gherkin
Scenario: New profile is signed in automatically
Given a visitor is not signed in
When they create a student profile with a WSU email and password
Then they are signed in
And they see their new profile page
```

```gherkin
Scenario: Email already has a profile
Given a profile exists for "alex.rivera@wsu.edu"
When a visitor creates a profile with the email "Alex.Rivera@wsu.edu"
Then the profile is not created
And they are told that a profile already exists for this email
```

```gherkin
Scenario: Email is not a WSU email
Given a visitor is creating a student profile
When they enter an email that does not end in "@wsu.edu"
Then the profile is not created
And they are told to use their WSU email
```

```gherkin
Scenario: Signed-out visitor opens My student profile
Given a visitor is not signed in
When they click "My student profile"
Then they are redirected to the sign-in page
And the sign-in page links to "Create a student profile" and back to home
```

```gherkin
Scenario: Sign out
Given the student is signed in
When they sign out
Then their session ends
And pages that need sign-in redirect to the sign-in page again
```

### FR-010.2 Front End: Sign-in page (#55)

```gherkin
Feature: Front End: Sign-in page

Scenario: Sign-in page shows email and password fields
Given the student is not signed in
When they open the sign-in page
Then they see WSU email and password fields and a sign-in button
```

```gherkin
Scenario: Invalid sign-in shows an error
Given the student enters credentials that do not match a profile
When they click sign in
Then the page says a valid school account is required
And the email they typed stays in the field
```

```gherkin
Scenario: Signed-in name shows in the header
Given the student is signed in
When they open any page
Then their name appears in the header
```

```gherkin
Scenario: Signed-out header shows a sign-in link
Given a visitor is not signed in
When they open the home page
Then the header shows a sign-in link instead of a name
```

```gherkin
Scenario: Sign out returns to the home page
Given the student is signed in
When they click sign out
Then they return to the home page, signed out
```
