# Implementation Plan

## Tasks

### FR-006.1 Front End: Navigation and error pages (#56, Yohann)
- [x] `fragments/nav.html`: the navigation header with the links from FR-006-01, as the fragment `header`
- [x] Delete `fragments/details.html`, the unused placeholder that `nav.html` replaces
- [x] Insert the header as the first element of `<body>` in every page: `index.html`, `studentForm.html`, `student-profile.html`, `clubForm.html`, `club.html` and `clubs.html`. Skip `no-profile.html`: it can't be reached once sign-in (FR-010) is merged, and FR-010 removes it
- [x] Remove the "Back to home" links from `studentForm.html`, `student-profile.html`, `clubForm.html`, `club.html` and `clubs.html`, now that the header has "Home".
- [x] `index.html`: keep the list of destinations in step with the header (FR-006-02), and change the title to "ClubHub"
- [x] `error/404.html`: add the header, say "This page could not be loaded", and use `th:href="@{/}"` for the link home
- [x] `error.html` (new): the general error page with the header, `${status}` and `${error}`, a short explanation and a link home
- [x] `styles.css`: style the header as a single row of links across the top of the page
- [x] Check every page title follows "Page name - ClubHub" (FR-006-07)
- [x] Check by hand in a browser: every header link from every page, an unknown URL, `/clubs/999`, `/students/999` and `/clubs/search/area?area=NOT_AN_AREA`

### Coordination with FR-010.2 Front End: Sign-in page (#55, Yohann)
- [x] Whichever of FR-006.1 and FR-010.2 merges second adds its part of the header to `nav.html`: the first name, sign-out button and "Sign in" link come from FR-010.2
- [x] "Sign in" link (FR-006-08): add it to `nav.html` only once `/login` exists, next to "Create a student profile", both inside `sec:authorize="isAnonymous()"`, and put "My student profile" inside `sec:authorize="isAuthenticated()"` (FR-006-01)
- [x] Insert the header in `login.html` too, which FR-010.2 adds

### Tests
- [x] `validation.md` for this folder (SDD-5, #48, Daniel)
- [ ] Automated tests from `validation.md` (TEST-012, #61, Daniel), including the test that opens every header link (NFR-006-04) and the 404, 400 and "no technical details" checks

## Files to Create or Modify

All paths are under `code/backend/`.

- `src/main/resources/templates/fragments/nav.html` (new): the shared navigation header
- `src/main/resources/templates/fragments/details.html` (delete): the unused placeholder
- `src/main/resources/templates/error.html` (new): the general error page for every status other than 404
- `src/main/resources/templates/error/404.html`: the header, the "could not be loaded" message and a `th:href` link home
- `src/main/resources/templates/index.html`: the header, the destination list and the "ClubHub" title
- `src/main/resources/templates/studentForm.html`, `student-profile.html`, `clubForm.html`, `club.html` and `clubs.html`: insert the header and remove the "Back to home" link
- `src/main/resources/static/styles.css`: styles for the header
- `src/test/java/com/clubhub/NavigationTests/` (new, TEST-012, Daniel): navigation and error page tests

## Dependencies

- **Libraries:** none new. Thymeleaf 3.1 fragments (`th:replace`) and Spring Boot's error handling (`BasicErrorController`) are already in `spring-boot-starter-thymeleaf` and `spring-boot-starter-web`.
- **Features:**
  - FR-001, FR-002 and FR-003: the pages the header links to.
  - FR-010 User Authentication (#5): adds the signed-in parts of the header (FR-010-11) and keeps `/error` public (FR-010-05). Either feature can merge first.
- **Work that depends on this feature:**
  - FR-004.2 Member management page (#57), FR-002.8 My Club Requests (#58) and every later page insert the same header and add their link to `nav.html` if they need one.
