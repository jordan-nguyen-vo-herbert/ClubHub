# Feature Validation

Sign-in (FR-010 User Authentication, #5). There is one test case for each Gherkin scenario in `requirements.md`. TC-010-01 to TC-010-11 cover FR-010.1 Back End: Sign-in (#49), and TC-010-12 to TC-010-16 cover FR-010.2 Front End: Sign-in page (#55). The automated tests for these cases are TEST-011 (#60).

Test cases are numbered TC-010-xx, after the feature's requirement IDs (FR-010-xx), so they don't clash with the test cases of other features.

**Test data.** The students come from `SampleStudents` in `code/backend/src/test/java/com/clubhub/StudentTests/`. "Butch's profile" means a profile with WSU ID 011111111, email butch.cougar@wsu.edu and password GoCougs1!, created through the profile form so its password is stored as a BCrypt hash.

**Status.** A case is Not run until its automated test passes on `main`. After that it is Pass or Fail.

## Test Case: TC-010-01 Successful sign-in
**Related Requirement:** FR-010-01, FR-010-06 (FR-010.1, #49)  
**Preconditions:** Butch's profile exists. The browser is signed out and hasn't tried to open a page that needs sign-in.

**Steps:**
1. Open `/login`.
2. Enter butch.cougar@wsu.edu as the WSU email and GoCougs1! as the password.
3. Click "Sign in".

**Expected Result:** Butch is signed in and the browser is redirected to the home page (`/`).  
**Status:** Not run

## Test Case: TC-010-02 Sign-in with invalid credentials
**Related Requirement:** FR-010-07, NFR-010-02 (FR-010.1, #49)  
**Preconditions:** Butch's profile exists. No profile exists for nobody@wsu.edu. The browser is signed out.

**Steps:**
1. Open `/login`, enter butch.cougar@wsu.edu with the password not-the-password, and click "Sign in".
2. Open `/login`, enter nobody@wsu.edu with any password, and click "Sign in".

**Expected Result:** Both attempts are refused and the browser stays signed out. Each is redirected to `/login?error`, which says "A valid school account is required". The two attempts get exactly the same response, so the page never reveals whether an email has a profile.  
**Status:** Not run

## Test Case: TC-010-03 Signed-out student opens a page that needs sign-in
**Related Requirement:** FR-010-05 (FR-010.1, #49)  
**Preconditions:** The browser is signed out.

**Steps:**
1. Open `/clubs/search`.
2. Open `/clubForm`.
3. Open `/`, `/login` and `/studentForm`.

**Expected Result:** Steps 1 and 2 are redirected to `/login`, and neither the search page nor the registration form is shown. The public pages in step 3 open normally with HTTP 200.  
**Status:** Not run

## Test Case: TC-010-04 Sign-in returns to the page the student wanted
**Related Requirement:** FR-010-06 (FR-010.1, #49)  
**Preconditions:** Butch's profile exists. The browser is signed out.

**Steps:**
1. Open `/clubs/search`. The browser is sent to `/login`.
2. Sign in as butch.cougar@wsu.edu with GoCougs1!.

**Expected Result:** After signing in, the browser is taken to `/clubs/search`, not the home page.  
**Status:** Not run

## Test Case: TC-010-05 Passwords are stored hashed
**Related Requirement:** FR-010-02, NFR-010-01 (FR-010.1, #49)  
**Preconditions:** No profile exists for butch.cougar@wsu.edu. The browser is signed out.

**Steps:**
1. Open `/studentForm`.
2. Fill in Butch's details with the password GoCougs1! and submit the form.
3. Read the stored password for butch.cougar@wsu.edu (`StudentRepository.findByEmail`).

**Expected Result:** The stored password is not GoCougs1!. It starts with a BCrypt prefix (`$2a$`, `$2b$` or `$2y$`), and the BCrypt encoder confirms it matches GoCougs1!. Neither the password nor its hash appears on the profile page.  
**Status:** Not run

## Test Case: TC-010-06 Existing plain-text passwords are hashed at startup
**Related Requirement:** FR-010-02 (FR-010.1, #49)  
**Preconditions:** A profile for Priya Patel (WSU ID 022222222, priya.patel@wsu.edu) is saved straight to the database with the plain-text password Pass2word, which is not a BCrypt hash.

**Steps:**
1. Start ClubHub, or run `PasswordMigration`, which runs at every startup.
2. Read Priya's stored password.
3. Sign in as priya.patel@wsu.edu with Pass2word.
4. Run `PasswordMigration` again and read the stored password once more.

**Expected Result:** After step 1 the stored password is a BCrypt hash that matches Pass2word. The sign-in in step 3 succeeds. Step 4 leaves the hash unchanged.  
**Status:** Not run

## Test Case: TC-010-07 New profile is signed in automatically
**Related Requirement:** FR-010-04 (FR-010.1, #49)  
**Preconditions:** No profile exists for butch.cougar@wsu.edu. The browser is signed out.

**Steps:**
1. Open `/studentForm`.
2. Fill in Butch's details and submit the form.
3. Open `/profile`.

**Expected Result:** Step 2 redirects to the new profile at `/students/{id}`, which shows Butch's details. The browser is signed in as butch.cougar@wsu.edu, so step 3 opens the same profile without asking to sign in.  
**Status:** Not run

## Test Case: TC-010-08 Email already has a profile
**Related Requirement:** FR-010-03 (FR-010.1, #49)  
**Preconditions:** A profile exists for Alex Rivera (WSU ID 033333333, alex.rivera@wsu.edu). The browser is signed out.

**Steps:**
1. Open `/studentForm`.
2. Fill in a new profile with the WSU ID 055555555 and the email Alex.Rivera@wsu.edu, and submit the form.

**Expected Result:** The profile is not created, and the number of profiles doesn't change. The form is shown again with "A profile already exists for this email" next to the email field.  
**Status:** Not run

## Test Case: TC-010-09 Email is not a WSU email
**Related Requirement:** FR-010-03 (FR-010.1, #49)  
**Preconditions:** The browser is signed out.

**Steps:**
1. Open `/studentForm`.
2. Fill in Butch's details with the email butch.cougar@gmail.com, and submit the form.

**Expected Result:** The profile is not created. The form is shown again with a message next to the email field telling the student to use their WSU email ending in @wsu.edu.  
**Status:** Not run

## Test Case: TC-010-10 Signed-out visitor opens My student profile
**Related Requirement:** FR-010-05, FR-010-09, FR-010-10 (FR-010.1, #49)  
**Preconditions:** The browser is signed out.

**Steps:**
1. Open `/`.
2. Click "My student profile" (`/profile`). Once FR-006.1 hides this link from signed-out visitors (FR-006-01), open `/profile` directly instead, as from a bookmark.

**Expected Result:** The browser is redirected to `/login`. The sign-in page links to "Create a student profile" (`/studentForm`) and back to the home page (`/`).  
**Status:** Not run

## Test Case: TC-010-11 Sign out
**Related Requirement:** FR-010-08 (FR-010.1, #49)  
**Preconditions:** Butch is signed in.

**Steps:**
1. Sign out (`POST /logout`).
2. Open `/profile`.

**Expected Result:** Step 1 ends the session and sends the browser to `/`, signed out. Step 2 redirects to `/login`.  
**Status:** Not run

## Test Case: TC-010-12 Sign-in page shows email and password fields
**Related Requirement:** FR-010-10 (FR-010.2, #55)  
**Preconditions:** The browser is signed out.

**Steps:**
1. Open `/login`.

**Expected Result:** The page loads with HTTP 200 and shows a WSU email field (`email`), a password field (`password`, which hides what is typed), a "Sign in" button, and links to create a student profile and to return home.  
**Status:** Not run

## Test Case: TC-010-13 Invalid sign-in shows an error
**Related Requirement:** FR-010-07 (FR-010.2, #55)  
**Preconditions:** Butch's profile exists. The browser is signed out.

**Steps:**
1. Open `/login`.
2. Enter butch.cougar@wsu.edu with the password not-the-password.
3. Click "Sign in".

**Expected Result:** The sign-in page is shown again with "A valid school account is required", shown once. The email field still contains butch.cougar@wsu.edu, and the password field is empty. The email does not appear in the URL.  
**Status:** Not run

## Test Case: TC-010-14 Signed-in name shows in the header
**Related Requirement:** FR-010-11 (FR-010.2, #55)  
**Preconditions:** Butch is signed in.

**Steps:**
1. Open `/`.
2. Open `/clubs/search`.
3. Open `/profile`.

**Expected Result:** On every page, the header shows the first name "Butch" and a "Sign out" button, and no "Sign in" link.  
**Status:** Not run

## Test Case: TC-010-15 Signed-out header shows a sign-in link
**Related Requirement:** FR-010-11, FR-006-08 (FR-010.2, #55)  
**Preconditions:** The browser is signed out.

**Steps:**
1. Open `/`.

**Expected Result:** The header shows a "Sign in" link to `/login`, and no first name or "Sign out" button.  
**Status:** Not run

## Test Case: TC-010-16 Sign out returns to the home page
**Related Requirement:** FR-010-08 (FR-010.2, #55)  
**Preconditions:** Butch is signed in and on `/clubs/search`.

**Steps:**
1. Click "Sign out" in the header.

**Expected Result:** The browser goes to the home page (`/`), signed out. The header shows the "Sign in" link instead of "Butch".  
**Status:** Not run
