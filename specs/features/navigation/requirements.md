# Feature Requirements

Navigation and error pages (FR-006 Working Webpage/User Interface, #10), a single item on the board: FR-006.1 Front End: Navigation and error pages (#56). Every page gets the same navigation header, and a page that can't be loaded explains what happened and links back home.

## User Story: US-05 (FR-006.1 Front End: Navigation and error pages, #56)

**As a** ClubHub user,  
**I need** to interact with the webpage's user interface,  
**so that** I can navigate the website and access its various directories.

### Details and Assumptions
- The ClubHub homepage provides navigation links to subdirectories.
- Each working navigation link opens the corresponding webpage.
- Users can return to the homepage when a webpage is unavailable.

## Functional Requirement: FR-006-01
Every page shall show the same navigation header. It shall always link to Home (`/`), Search clubs (`/clubs/search`) and Register a club (`/clubForm`). The remaining links depend on sign-in:

- Before sign-in (FR-010) is merged, it shall also link to Create a student profile (`/studentForm`) and My student profile (`/profile`).
- Once FR-010 is merged, a signed-out visitor shall also see Create a student profile and Sign in (FR-006-08), and a signed-in student shall also see My student profile, their first name and Sign out (FR-010-11). Create a student profile is hidden from signed-in students, because creating a profile while signed in would make a second account and sign the browser in as the new student.

## Functional Requirement: FR-006-02
The home page shall list the same destinations as the navigation header, each with a link.

## Functional Requirement: FR-006-03
Each navigation link shall open its page. Once sign-in (FR-010) is merged, a link to a page that needs sign-in shall take a signed-out visitor to the sign-in page instead (FR-010-05).

## Functional Requirement: FR-006-04
When a page can't be found, the system shall return HTTP 404 and show a "Page not found" page that says "This page could not be loaded" and links back to the home page. This covers a URL that doesn't exist and a club or student ID that doesn't exist.

## Functional Requirement: FR-006-05
For any other error, including a bad request (400), a refused request (403) and a server error (500), the system shall return that HTTP status and show a general error page with the status code, a short explanation and a link back to the home page. Spring Boot's default "Whitelabel Error Page" shall never be shown.

## Functional Requirement: FR-006-06
Every error page shall show the navigation header from FR-006-01.

## Functional Requirement: FR-006-07
Every page title shall follow the pattern "Page name - ClubHub", and the home page title shall be "ClubHub", so the browser tab shows where the user is.

## Functional Requirement: FR-006-08
Once sign-in (FR-010) is merged, the navigation header shall show a signed-out visitor a "Sign in" link to ClubHub's sign-in page (`/login`). This is the same link as FR-010-11.

## Non-Functional Requirement: NFR-006-01
The navigation header shall be defined once, in `templates/fragments/nav.html`, so changing a link means editing one file.

## Non-Functional Requirement: NFR-006-02
Every page shall be reachable from the home page in at most two clicks.

## Non-Functional Requirement: NFR-006-03
An error page shall never show a stack trace, an exception message, a Java class name or SQL.

## Non-Functional Requirement: NFR-006-04
An automated test shall open every link in the navigation header and check that it returns HTTP 200 (or, after FR-010, a redirect to the sign-in page), so a broken link fails the build.

## Non-Functional Requirement: NFR-006-05
Pages shall display correctly in current desktop versions of Chrome, Edge and Firefox at a window width of 1024 pixels or more. A mobile layout is out of scope (`mission.md`).

## Acceptance Criteria

### FR-006.1 Front End: Navigation and error pages (#56)

#### From the user story (#56)

```gherkin
Feature: Front End: Navigation and error pages

Scenario: Navigate to an available subdirectory
Given the user is on the ClubHub homepage
When the user clicks a navigation link to a subdirectory
Then the user is taken to the corresponding subdirectory
And the corresponding webpage loads properly
```

```gherkin
Scenario: Navigate to an unavailable directory
Given the user is trying to access a ClubHub webpage
When the user clicks a navigation link for an unavailable directory
Then the system displays that the webpage could not be loaded
And the user is given an option or link to return to the ClubHub homepage
```

#### Extra scenarios

These seven are not part of the original user story. They were added so that each functional and non-functional requirement above has a scenario to test against.

Once sign-in (FR-010) is merged, a signed-out visitor who opens a protected or unknown URL is sent to the sign-in page before any 404 or 400 can happen (FR-010-05). That is why the three error scenarios below need a signed-in user once FR-010 is merged.

```gherkin
Scenario: Navigation header appears on every page
Given the user is on any ClubHub page
When the page loads
Then the navigation header shows links to Home, Search clubs and Register a club
And it shows the sign-in links described in FR-006-01
```

```gherkin
Scenario: Return home from any page
Given the user is on any ClubHub page
When the user clicks "Home" in the navigation header
Then the user is taken to the ClubHub homepage
```

```gherkin
Scenario Outline: Header links follow the sign-in state
Given sign-in (FR-010) is merged
And the user is <state>
When the user opens any ClubHub page
Then the navigation header shows <shown>
And it does not show <hidden>

Examples:
  | state         | shown                                                   | hidden                                     |
  | not signed in | "Create a student profile" and "Sign in"                | "My student profile" or "Sign out"         |
  | signed in     | "My student profile", their first name and "Sign out"   | "Create a student profile" or "Sign in"    |
```

```gherkin
Scenario: Page does not exist
Given the user is using ClubHub
And the user is signed in, once sign-in (FR-010) is merged
When the user opens a URL that does not exist, such as "/clubs/abc/xyz"
Then the system responds with HTTP 404
And the page says "This page could not be loaded"
And the page links back to the ClubHub homepage
```

```gherkin
Scenario: Club or student does not exist
Given no club has the ID 999
And the user is signed in, once sign-in (FR-010) is merged
When the user opens "/clubs/999"
Then the system responds with HTTP 404
And the page says "This page could not be loaded"
And the page links back to the ClubHub homepage
```

```gherkin
Scenario: Bad request shows the general error page
Given the user is on the club search page
And the user is signed in, once sign-in (FR-010) is merged
When the user opens "/clubs/search/area?area=NOT_AN_AREA"
Then the system responds with HTTP 400
And the general error page shows the status code and a link back to the ClubHub homepage
And the Whitelabel Error Page is not shown
```

```gherkin
Scenario: Error pages hide technical details
Given an error occurs while loading a page
When the error page is shown
Then it does not show a stack trace, an exception message or a Java class name
```
