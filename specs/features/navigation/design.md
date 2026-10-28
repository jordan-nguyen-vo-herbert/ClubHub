# Feature Design

## Related Requirements
FR-006-01 to FR-006-08, NFR-006-01 to NFR-006-05 (see `requirements.md`)

## UML Diagrams

The diagrams are written in Mermaid, which GitHub draws directly in this file.

### Sequence: following a navigation link

```mermaid
sequenceDiagram
    actor User
    participant Browser
    participant CC as ClubController
    participant TL as Thymeleaf
    participant Nav as fragments/nav.html

    User->>Browser: Click "Search clubs" in the navigation header
    Browser->>CC: GET /clubs/search
    CC-->>TL: View name "clubs"
    TL->>Nav: th:replace="~{fragments/nav :: header}"
    Nav-->>TL: Navigation header HTML
    TL-->>Browser: HTTP 200, clubs.html with the header
    Browser-->>User: Search Clubs page
```

### Sequence: opening a page that can't be loaded

```mermaid
sequenceDiagram
    actor User
    participant Browser
    participant MVC as Spring MVC
    participant CC as ClubController
    participant EC as BasicErrorController (/error)
    participant TL as Thymeleaf

    User->>Browser: Open /clubs/999
    Browser->>MVC: GET /clubs/999
    MVC->>CC: clubPage(999)
    CC-->>MVC: ResponseStatusException(404, "Club not found")
    MVC->>EC: Forward to /error with status 404
    EC->>TL: Resolve the view error/404
    TL-->>Browser: HTTP 404, error/404.html with the header and a link home
    Browser-->>User: "This page could not be loaded"

    Note over MVC,EC: An unknown URL has no controller method, so Spring MVC<br/>raises NoResourceFoundException (404) and the same path follows
```

### Activity: how Spring Boot picks the error page

```mermaid
flowchart TD
    A[Request fails with an HTTP status] --> B{templates/error/STATUS.html exists?<br/>for example error/404.html}
    B -- yes --> P1[Show that page]
    B -- no --> C{templates/error/4xx.html or 5xx.html exists?}
    C -- yes --> P2[Show that page]
    C -- no --> D{templates/error.html exists?}
    D -- yes --> P3[Show the general error page]
    D -- no --> W[Whitelabel Error Page]

    style W stroke-dasharray: 5 5
```

ClubHub provides `error/404.html` and `error.html`, so every status ends at one of those two and the Whitelabel page is never reached (FR-006-05).

### Component diagram: templates and the shared header

```mermaid
classDiagram
    class nav_html {
        <<fragment>>
        Home
        Search clubs
        Register a club
        Create a student profile or My student profile
        Sign in or Sign out, after FR-010
    }
    class index_html
    class studentForm_html
    class student_profile_html
    class clubForm_html
    class club_html
    class clubs_html
    class error_404_html {
        status 404
        This page could not be loaded
        Back to home link
    }
    class error_html {
        any other status
        status code and explanation
        Back to home link
    }

    index_html ..> nav_html : inserts header
    studentForm_html ..> nav_html : inserts header
    student_profile_html ..> nav_html : inserts header
    clubForm_html ..> nav_html : inserts header
    club_html ..> nav_html : inserts header
    clubs_html ..> nav_html : inserts header
    error_404_html ..> nav_html : inserts header
    error_html ..> nav_html : inserts header
```

`no-profile.html` is left out: once sign-in (FR-010) is merged, `/profile` always has a signed-in student, so that page can't be reached and FR-010 removes it. `login.html` from FR-010.2 also inserts the header once it exists.

## Design Notes

- **Main classes or components:**
  - `templates/fragments/nav.html` (new): the navigation header, written once and inserted into every page. It replaces the empty placeholder `fragments/details.html`.
  - `templates/error/404.html` (changed): the "Page not found" page.
  - `templates/error.html` (new): the general error page for every other status.
  - Every other template (changed): inserts the header and follows the title pattern.
  - `static/styles.css` (changed): styles for the header.
  - No Java changes. `ClubController` and `StudentController` already return 404 for a missing club or student (`HomeController` only maps `/`), and Spring Boot's `BasicErrorController` already renders `error/...` templates.
- **Responsibilities:**
  - `nav.html` holds the only copy of the navigation links (NFR-006-01). Each link uses `th:href="@{...}"`.
  - `error/404.html` covers every "not found" case: unknown URLs and missing clubs or students (FR-006-04).
  - `error.html` covers every other status (FR-006-05). It shows `${status}` and `${error}`, which Spring Boot adds to the model, for example "400" and "Bad Request".
  - Both error pages show the header and a "Back to home" link (FR-006-06), so the user can always leave.
  - Normal pages rely on the header's "Home" link, so their own "Back to home" links are removed. Only the two error pages keep a separate "Back to home" link, because a user who lands on an error page needs the way out to be obvious.
- **Important interactions (fixed names for front-end and test work):**

  | What | Name or URL |
  |---|---|
  | Header fragment | `templates/fragments/nav.html`, fragment name `header` |
  | Inserting it | `<header th:replace="~{fragments/nav :: header}"></header>` as the first element in `<body>` |
  | Links shown to everyone | "Home" `/`, "Search clubs" `/clubs/search`, "Register a club" `/clubForm` |
  | Links before FR-010 | "Create a student profile" `/studentForm`, "My student profile" `/profile` |
  | Signed-out links (after FR-010) | "Create a student profile" `/studentForm` and "Sign in" `/login`, inside `sec:authorize="isAnonymous()"` |
  | Signed-in links (after FR-010) | "My student profile" `/profile`, the first name and the "Sign out" button, inside `sec:authorize="isAuthenticated()"` (FR-010-11) |
  | Not-found page | `templates/error/404.html`, HTTP 404, text "This page could not be loaded" |
  | General error page | `templates/error.html`, the original HTTP status, shows `${status}` and `${error}` |
  | Link home on error pages | `<a th:href="@{/}">Back to home</a>` |
  | Page titles | "Page name - ClubHub"; the home page is "ClubHub" |

- **Assumptions:**
  - Spring Boot's defaults stay as they are: `server.error.include-stacktrace=never`, `server.error.include-message=never` and `server.error.include-exception=false`. They are what keep technical details off the error pages (NFR-006-03), so nobody should turn them on.
  - The "unavailable directory" in the Gherkin scenario means any page that can't be loaded: a mistyped or outdated URL, a club or student that doesn't exist, or a request the server can't handle.
  - Sign-in (FR-010) owns the signed-in parts of the header: the first name, the sign-out button and the "Sign in" link (FR-010-11, FR-006-08). Whichever feature merges first creates `nav.html`, and the other adds to it. Until FR-010 is merged, the header has no "Sign in" link, because `/login` doesn't exist yet and the link would lead to the 404 page, and it uses no `sec:` attributes.
  - Once FR-010 is merged, `/error` stays public (FR-010-05). That lets the error page itself render for any visitor, for example a 403 from a missing CSRF token lands on `error.html`. But a signed-out visitor who opens a protected or unknown URL, such as `/clubs/999`, is sent to the sign-in page first, so only a signed-in student sees the 404 or 400 page for those URLs.
  - Desktop browsers only; no mobile layout (`mission.md`).
  - Not in this feature: a highlighted "current page" in the header, breadcrumbs, and a site-wide search box.
