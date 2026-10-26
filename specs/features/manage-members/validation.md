# Feature Validation

Manage members (FR-004, #12). There is one test case for each Gherkin scenario in `requirements.md`, all from FR-004.1 Back End: Manage members (#51). The automated tests for these cases are TEST-011 (#60).

Test cases are numbered TC-004-xx, after the feature's requirement IDs (FR-004-xx), so they don't clash with the test cases of other features. The URLs and button names are the fixed names in `design.md`.

**Test data.** The students come from `SampleStudents` in `code/backend/src/test/java/com/clubhub/StudentTests/`:

- Butch Cougar (WSU ID 011111111) founded the club "Chess Club", so he is its President, which is an officer role.
- Priya Patel (WSU ID 022222222) and Alex Rivera (WSU ID 033333333) have student profiles.

`{clubId}` is Chess Club's ID. Every action is done by the signed-in student (NFR-004-02).

**Status.** A case is Not run until its automated test passes on `main`. After that it is Pass or Fail.

## Test Case: TC-004-01 Successfully add a club member
**Related Requirement:** FR-004-01, NFR-004-02 (FR-004.1, #51)  
**Preconditions:** Butch is signed in and is the President of Chess Club. Priya is not a member.

**Steps:**
1. Open `/clubs/{clubId}/members`.
2. Enter 022222222 in the WSU ID field.
3. Click "Add Member".

**Expected Result:** The browser is redirected back to `/clubs/{clubId}/members`, and Priya appears in the member list with the member role. The club page (`/clubs/{clubId}`) lists her too.  
**Status:** Not run

## Test Case: TC-004-02 Add a member without required information
**Related Requirement:** FR-004-02 (FR-004.1, #51)  
**Preconditions:** Butch is signed in and is the President of Chess Club, which has no other members.

**Steps:**
1. Open `/clubs/{clubId}/members`.
2. Leave the WSU ID field blank.
3. Click "Add Member".

**Expected Result:** The member management page is shown again (HTTP 200) with an error message next to the WSU ID field. The member list is unchanged, with Butch as its only member.  
**Status:** Not run

## Test Case: TC-004-03 Remove a member
**Related Requirement:** FR-004-03 (FR-004.1, #51)  
**Preconditions:** Butch is signed in and is the President of Chess Club. Priya is a member.

**Steps:**
1. Open `/clubs/{clubId}/members`.
2. Click "Remove" next to Priya.

**Expected Result:** The browser is redirected back to `/clubs/{clubId}/members`. Priya no longer appears in the member list or on the club page. Her student profile still exists.  
**Status:** Not run

## Test Case: TC-004-04 Student requests to join
**Related Requirement:** FR-004-04 (FR-004.1, #51)  
**Preconditions:** Alex is signed in. He is not a member of Chess Club and has no pending request. Butch is the club's President.

**Steps:**
1. As Alex, open `/clubs/{clubId}` and click "Request to Join".
2. Sign out, sign in as Butch, and open `/clubs/{clubId}/members`.
3. Click "Accept" next to Alex's request.

**Expected Result:** After step 1, the request is saved and Alex no longer sees the "Request to Join" button. In step 2, Butch sees Alex's pending request. After step 3, Alex is in the member list and the request is no longer pending.  
**Status:** Not run

## Test Case: TC-004-05 Non-officer cannot manage members
**Related Requirement:** FR-004-05, NFR-004-01 (FR-004.1, #51)  
**Preconditions:** Priya is signed in and is a member of Chess Club, but not an officer. Alex is not a member.

**Steps:**
1. As Priya, send `POST /clubs/{clubId}/members` with the WSU ID 033333333.
2. As Priya, send `POST /clubs/{clubId}/members/{butchStudentId}/remove`.

The page hides these buttons from non-officers, so send the requests directly, for example from the automated test. Hiding a button is not what protects the action (NFR-004-01).

**Expected Result:** Both requests are refused with HTTP 403. Alex is not added, Butch is not removed, and the member list is unchanged.  
**Status:** Not run
