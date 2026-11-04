# Feature Validation

Signed-in club registration (FR-002 Club Registration). There is one test case for each Gherkin scenario in `requirements.md`: TC-002-01 to TC-002-07 cover FR-002.7 Back End: Signed-in club registration (#50), and TC-002-08 and TC-002-09 cover FR-002.8 Front End: Confirmation and My Club Requests (#58). The automated tests for these cases are in `ClubRegistrationValidationTest`, part of TEST-012 (#61).

Test cases are numbered TC-002-xx, after the feature's requirement IDs (FR-002-xx), so they don't clash with the test cases of other features. The URLs, messages and model attributes are the fixed names in `design.md`.

**Test data.** The students come from `SampleStudents` in `code/backend/src/test/java/com/clubhub/StudentTests/`. "Butch's profile" means a saved profile for Butch Cougar (WSU ID 011111111, butch.cougar@wsu.edu), and "Alex's profile" one for Alex Rivera (WSU ID 033333333, alex.rivera@wsu.edu). "A valid registration" means a club name that no club has yet, an area of interest from the list, a description and a contact email.

**Status.** A case is Not run until its automated test passes on `main`. After that it is Pass or Fail.

## Test Case: TC-002-01 Logged-in student opens the registration form
**Related Requirement:** FR-002-01, FR-002-02 (FR-002.7, #50)  
**Preconditions:** Butch's profile exists, and Butch is signed in.

**Steps:**
1. Open `/clubForm`.

**Expected Result:** The form loads with HTTP 200 and shows "Butch Cougar" and butch.cougar@wsu.edu as the founding officer, as plain text he can't change. There is no WSU ID field.  
**Status:** Not run

## Test Case: TC-002-02 Unauthenticated user requests the form
**Related Requirement:** FR-002-04 (FR-002.7, #50)  
**Preconditions:** The browser is signed out.

**Steps:**
1. Open `/clubForm`.

**Expected Result:** The browser is redirected to the sign-in page (`/login`), and the form is not shown.  
**Status:** Not run

## Test Case: TC-002-03 Unauthenticated submission
**Related Requirement:** FR-002-04 (FR-002.7, #50)  
**Preconditions:** The browser is signed out.

**Steps:**
1. Send a valid registration for "Signed Out Club" to `POST /clubForm`.

**Expected Result:** The browser is redirected to the sign-in page (`/login`). No club is saved, so the number of clubs doesn't change.  
**Status:** Not run

## Test Case: TC-002-04 Submitter can view their pending club
**Related Requirement:** FR-002-06 (FR-002.7, #50)  
**Preconditions:** Butch's profile exists, and Butch is signed in.

**Steps:**
1. Register "Robotics Club" with a valid registration.
2. Open the club's page, `/clubs/{clubId}`.

**Expected Result:** The page loads with HTTP 200 and shows "Robotics Club" with the status Pending.  
**Status:** Not run

## Test Case: TC-002-05 The founder is the signed-in student
**Related Requirement:** FR-002-01, FR-002-03, NFR-002-01 (FR-002.7, #50)  
**Preconditions:** Butch's and Alex's profiles exist, and Butch is signed in.

**Steps:**
1. Send a valid registration for "Founder Check Club" to `POST /clubForm`, with Alex's WSU ID added as `founderStudentNumber`.

**Expected Result:** The club is saved with the Pending status and Butch as its founder, not Alex. Butch is its only member, in the President role.  
**Status:** Not run

## Test Case: TC-002-06 Registration is confirmed
**Related Requirement:** FR-002-05 (FR-002.7, #50)  
**Preconditions:** Butch's profile exists, and Butch is signed in.

**Steps:**
1. Register "Chess Club" with a valid registration.
2. Follow the redirect to the new club's page.
3. Refresh the club's page.

**Expected Result:** Step 1 redirects to `/clubs/{clubId}`. In step 2, the page says the registration was submitted and is waiting for approval, and links to My Club Requests (`/clubs/mine`). In step 3, the message is gone, and Butch still has exactly one club.  
**Status:** Not run

## Test Case: TC-002-07 My Club Requests lists only the student's own clubs
**Related Requirement:** FR-002-07, NFR-002-02 (FR-002.7, #50)  
**Preconditions:** Butch's and Alex's profiles exist.

**Steps:**
1. As Butch, register "Older Club", then "Newer Club".
2. As Alex, register "Alex's Club".
3. As Butch, open `/clubs/mine`.

**Expected Result:** The page lists "Newer Club" before "Older Club", each with the status Pending and a link to its page. It does not list "Alex's Club".  
**Status:** Not run

## Test Case: TC-002-08 Confirmation after submitting
**Related Requirement:** FR-002-05 (FR-002.8, #58)  
**Preconditions:** Butch's profile exists, and Butch is signed in.

**Steps:**
1. Open `/clubForm`, fill in a valid registration and click "Register club".

**Expected Result:** The new club's page shows a success message saying the registration was submitted, with a link to My Club Requests (`/clubs/mine`).  
**Status:** Not run

## Test Case: TC-002-09 No requests yet
**Related Requirement:** FR-002-07 (FR-002.8, #58)  
**Preconditions:** Butch's profile exists, Butch is signed in, and he has not registered a club.

**Steps:**
1. Open `/clubs/mine`.

**Expected Result:** The page loads with HTTP 200 and says "No club requests yet", with a link to register a club (`/clubForm`).  
**Status:** Not run
