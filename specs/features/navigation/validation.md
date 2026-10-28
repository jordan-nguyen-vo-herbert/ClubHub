# Feature Validation

Navigation and error pages (FR-006 Working Webpage/User Interface, #10), all from FR-006.1 Front End: Navigation and error pages (#56). There is one test case for each Gherkin scenario in `requirements.md`: TC-006-01 and TC-006-02 come from the user story, and TC-006-03 to TC-006-09 from the extra scenarios. The automated tests for these cases are TEST-012 (#61).

Test cases are numbered TC-006-xx, after the feature's requirement IDs (FR-006-xx), so they don't clash with the test cases of other features. The page names, links and messages are the fixed names in `design.md`.

**Test data.** Butch Cougar (WSU ID 011111111, butch.cougar@wsu.edu) from `SampleStudents` has a student profile, and at least one club exists. `{clubId}` and `{studentId}` are the IDs of that club and of Butch.

**Sign-in.** Once sign-in (FR-010) is merged, a signed-out visitor who opens a protected or unknown URL is sent to the sign-in page before any 404 or 400 can happen (FR-010-05). From then on, every case runs with Butch signed in, unless it says otherwise.

**Status.** A case is Not run until its automated test passes on `main`. After that it is Pass or Fail.

## Test Case: TC-006-01 Navigate to an available subdirectory
**Related Requirement:** FR-006-02, FR-006-03, FR-006-07, NFR-006-04 (FR-006.1, #56)  
**Preconditions:** Butch is signed in.

**Steps:**
1. Open the home page (`/`).
2. Click each destination the home page lists: "Search clubs", "Register a club" and "My student profile".
3. Repeat step 2 with every link in the navigation header.

**Expected Result:** Each link opens its page with HTTP 200, and the page's title follows "Page name - ClubHub". No link leads to the not-found page.  
**Status:** Not run

## Test Case: TC-006-02 Navigate to an unavailable directory
**Related Requirement:** FR-006-04 (FR-006.1, #56)  
**Preconditions:** Butch is signed in and on the home page.

**Steps:**
1. Follow a link to a page that no longer exists, such as `/old-page`.

**Expected Result:** The system responds with HTTP 404 and shows the "Page not found" page, which says "This page could not be loaded" and has a "Back to home" link to `/`. Clicking that link opens the home page.  
**Status:** Not run

## Test Case: TC-006-03 Navigation header appears on every page
**Related Requirement:** FR-006-01, FR-006-06, NFR-006-01 (FR-006.1, #56)  
**Preconditions:** Butch is signed in for steps 1 to 3, and the browser is signed out for step 4.

**Steps:**
1. Open `/`, `/clubs/search`, `/clubForm`, `/clubs/{clubId}` and `/students/{studentId}`.
2. Open `/clubs/abc/xyz` to get the not-found page.
3. Open `/clubs/search/area?area=NOT_AN_AREA` to get the general error page.
4. Sign out, then open `/studentForm` and `/login`.

**Expected Result:** Every page, including both error pages, shows the same navigation header with links to Home (`/`), Search clubs (`/clubs/search`) and Register a club (`/clubForm`), plus the sign-in links from FR-006-01 (checked in TC-006-05).  
**Status:** Not run

## Test Case: TC-006-04 Return home from any page
**Related Requirement:** FR-006-01 (FR-006.1, #56)  
**Preconditions:** Butch is signed in.

**Steps:**
1. Open `/clubs/search` and click "Home" in the navigation header.
2. Repeat from `/clubForm` and `/clubs/{clubId}`.

**Expected Result:** Each click opens the home page (`/`), whose title is "ClubHub".  
**Status:** Not run

## Test Case: TC-006-05 Header links follow the sign-in state
**Related Requirement:** FR-006-01, FR-006-08, FR-010-11 (FR-006.1, #56)  
**Preconditions:** Sign-in (FR-010) is merged. The browser is signed out.

**Steps:**
1. Open `/` and `/studentForm`.
2. Sign in as Butch, then open `/` and `/clubs/search`.

**Expected Result:** In step 1, the header shows "Create a student profile" and "Sign in" (`/login`), and does not show "My student profile" or "Sign out". In step 2, the header shows "My student profile", the first name "Butch" and "Sign out", and does not show "Create a student profile" or "Sign in".  
**Status:** Not run

## Test Case: TC-006-06 Page does not exist
**Related Requirement:** FR-006-04 (FR-006.1, #56)  
**Preconditions:** Butch is signed in.

**Steps:**
1. Open `/clubs/abc/xyz`.

**Expected Result:** The system responds with HTTP 404. The page says "This page could not be loaded" and links back to the home page.  
**Status:** Not run

## Test Case: TC-006-07 Club or student does not exist
**Related Requirement:** FR-006-04 (FR-006.1, #56)  
**Preconditions:** Butch is signed in. No club has the ID 999, and no student has the ID 999.

**Steps:**
1. Open `/clubs/999`.
2. Open `/students/999`.

**Expected Result:** Both respond with HTTP 404. Each page says "This page could not be loaded" and links back to the home page.  
**Status:** Not run

## Test Case: TC-006-08 Bad request shows the general error page
**Related Requirement:** FR-006-05 (FR-006.1, #56)  
**Preconditions:** Butch is signed in and on the club search page.

**Steps:**
1. Open `/clubs/search/area?area=NOT_AN_AREA`.

**Expected Result:** The system responds with HTTP 400. The general error page shows the status code 400, a short explanation and a link back to the home page. The text "Whitelabel Error Page" does not appear.  
**Status:** Not run

## Test Case: TC-006-09 Error pages hide technical details
**Related Requirement:** NFR-006-03 (FR-006.1, #56)  
**Preconditions:** Butch is signed in.

**Steps:**
1. Open the error pages from TC-006-06, TC-006-07 and TC-006-08.
2. View each page's source.

**Expected Result:** No page contains a stack trace, an exception message, a Java class name or SQL. For example, none contains "Exception", "java.", "com.clubhub" or "select ".  
**Status:** Not run
