# Feature Design

## Related Requirements
FR-004-01 to FR-004-05, NFR-004-01 to NFR-004-04 (see `requirements.md`)

## UML Diagrams

The diagrams are written in Mermaid, which GitHub draws directly in this file.

### Sequence: adding a member (FR-004-01)

```mermaid
sequenceDiagram
    actor Officer
    participant Browser
    participant MC as MemberController
    participant SR as StudentRepository
    participant MR as MemberRepository
    participant TL as Thymeleaf

    Officer->>Browser: Enter a WSU ID and click "Add Member"
    Browser->>MC: POST /clubs/5/members (wsuId)
    MC->>MC: requireOfficer(club, signedInStudent)
    MC->>SR: findByWsuId(wsuId)
    SR-->>MC: Student
    MC->>MR: save(Member with the role MEMBER)
    MR-->>MC: saved
    MC-->>Browser: Redirect to /clubs/5/members
    Browser->>MC: GET /clubs/5/members
    MC->>TL: View name "clubMembers"
    TL-->>Browser: HTTP 200, member list including the new member
    Browser-->>Officer: Club member management page
```

### Sequence: adding a member without required information (FR-004-02)

```mermaid
sequenceDiagram
    actor Officer
    participant Browser
    participant MC as MemberController
    participant TL as Thymeleaf

    Officer->>Browser: Leave the WSU ID blank and click "Add Member"
    Browser->>MC: POST /clubs/5/members (wsuId is empty)
    MC->>MC: Bean Validation fails on MemberForm (@NotBlank)
    MC-->>TL: View name "clubMembers" with the field error
    TL-->>Browser: HTTP 200, same page with an error message
    Browser-->>Officer: Error message, member list unchanged
```

### Sequence: removing a member (FR-004-03)

```mermaid
sequenceDiagram
    actor Officer
    participant Browser
    participant MC as MemberController
    participant MR as MemberRepository

    Officer->>Browser: Click "Remove" next to a member
    Browser->>MC: POST /clubs/5/members/42/remove
    MC->>MC: requireOfficer(club, signedInStudent)
    MC->>MR: delete the Member for club 5 and student 42
    MR-->>MC: deleted
    MC-->>Browser: Redirect to /clubs/5/members
    Browser-->>Officer: Member list without that student
```

### Sequence: requesting to join and accepting the request (FR-004-04)

```mermaid
sequenceDiagram
    actor Student
    actor Officer
    participant Browser
    participant MC as MemberController
    participant JR as JoinRequestRepository
    participant MR as MemberRepository

    Student->>Browser: Click "Request to Join" on the club page
    Browser->>MC: POST /clubs/5/join-requests
    MC->>JR: save(JoinRequest with status PENDING)
    JR-->>MC: saved
    MC-->>Browser: Redirect to /clubs/5

    Officer->>Browser: Open the member management page
    Browser->>MC: GET /clubs/5/members
    MC->>MC: requireOfficer(club, signedInStudent)
    MC->>JR: find the pending requests for club 5
    JR-->>MC: Pending requests
    MC-->>Browser: HTTP 200, page lists the request

    Officer->>Browser: Click "Accept"
    Browser->>MC: POST /clubs/5/join-requests/7/accept
    MC->>MC: requireOfficer(club, signedInStudent)
    MC->>MR: save(Member with the role MEMBER)
    MC->>JR: delete the request
    MC-->>Browser: Redirect to /clubs/5/members
```

### Sequence: a non-officer tries to manage members (FR-004-05)

```mermaid
sequenceDiagram
    actor Student
    participant Browser
    participant MC as MemberController
    participant EC as BasicErrorController (/error)
    participant TL as Thymeleaf

    Student->>Browser: Submit an add or remove request for a club they do not run
    Browser->>MC: POST /clubs/5/members/42/remove
    MC->>MC: requireOfficer fails
    MC-->>EC: ResponseStatusException(403), forwarded to /error
    EC->>TL: Resolve the view error (general error page)
    TL-->>Browser: HTTP 403, error.html with the header and a link home
    Browser-->>Student: Status 403 and a link back to home
```

### Activity: who may do what

```mermaid
flowchart TD
    A[Request to a member action] --> B{Signed in?}
    B -- no --> L[Redirect to sign-in page, FR-010]
    B -- yes --> C{Which action?}
    C -- Request to Join --> F[Save a PENDING join request]
    C -- Add, remove, accept, or view requests --> G{Signed-in student is<br/>PRESIDENT or OFFICER of this club?}
    G -- no --> H[HTTP 403, general error page]
    G -- yes --> I[Run the action and redirect to the member page]
```

### Class diagram: domain and web layer

```mermaid
classDiagram
    class Club {
        Long id
        String name
    }
    class Student {
        Long id
        String wsuId
        String firstName
    }
    class Member {
        ClubRole role
    }
    class ClubRole {
        <<enumeration>>
        PRESIDENT
        OFFICER
        MEMBER
        isOfficer() boolean
    }
    class JoinRequest {
        Long id
        RequestStatus status
    }
    class RequestStatus {
        <<enumeration>>
        PENDING
    }
    class MemberForm {
        String wsuId
    }
    class MemberResponse {
        Long studentId
        String name
        String wsuId
        ClubRole role
    }
    class MemberController {
        membersPage(clubId)
        addMember(clubId, form)
        removeMember(clubId, studentId)
        requestToJoin(clubId)
        acceptRequest(clubId, requestId)
        requireOfficer(club, student)
    }
    class MemberRepository {
        <<interface>>
    }
    class JoinRequestRepository {
        <<interface>>
    }

    Member "*" --> "1" Club
    Member "*" --> "1" Student
    Member --> ClubRole
    JoinRequest "*" --> "1" Club
    JoinRequest "*" --> "1" Student
    JoinRequest --> RequestStatus
    MemberController ..> MemberForm : binds and validates
    MemberController ..> MemberResponse : builds the member list
    MemberController --> MemberRepository
    MemberController --> JoinRequestRepository
```

## Design Notes

- **Main classes or components:**
  - `controller/MemberController` (new): maps the member management page and the add, remove, request and accept actions. It holds the officer check.
  - `entities/Member` and `entities/ClubRole` (existing): a student's place in a club and the role. `ClubRole.isOfficer()` is true for `PRESIDENT` and `OFFICER`. The founder is saved as `PRESIDENT` when a club is registered.
  - `entities/JoinRequest` and `entities/RequestStatus` (new): a student's pending request to join a club.
  - `dto/MemberForm` (new): the WSU ID typed by the officer, with `@NotBlank`.
  - `dto/MemberResponse` (changed): one row of the member list. `dto/JoinRequestResponse` (new): one row of the pending requests.
  - `repository/JoinRequestRepository` (new) and `repository/MemberRepository` (new, only if `Member` is its own entity today). If `Member` lives inside `Club`, the controller saves through `ClubRepository` instead.
  - `templates/clubMembers.html` (new): the member management page.
  - `templates/club.html` (changed): the "Request to Join" button and an officer-only "Manage members" link.
  - `controller/ClubController` (changed, if needed): saves the founder as `PRESIDENT` and tells `club.html` whether the signed-in student is an officer, a member or has a pending request.
- **Responsibilities:**
  - `requireOfficer(club, student)` is the only place that decides who may change members or accept requests (NFR-004-01). Every officer-only action calls it first, so no action can skip the check.
  - A refused change throws `ResponseStatusException(403)`, the same way `ClubController` already throws 404, so the general error page from FR-006 (`error.html`) shows it. Nothing is saved or deleted.
  - A blank WSU ID shows an error message on the member page and leaves the list unchanged (FR-004-02).
  - Every successful change redirects to `GET /clubs/{id}/members` (post-redirect-get), so a refresh does not repeat the change.
  - The acting student comes from the sign-in (FR-010.1), never from a form field or URL (NFR-004-02).
- **Important interactions (fixed names for front-end and test work):**

  | What | Name or URL |
  |---|---|
  | Member management page | `GET /clubs/{clubId}/members`, template `clubMembers.html` |
  | Add a member | `POST /clubs/{clubId}/members`, field `wsuId`, button "Add Member" |
  | Remove a member | `POST /clubs/{clubId}/members/{studentId}/remove`, button "Remove" |
  | Request to join | `POST /clubs/{clubId}/join-requests`, button "Request to Join" on `club.html` |
  | Accept a request | `POST /clubs/{clubId}/join-requests/{requestId}/accept`, button "Accept" |
  | Roles | `PRESIDENT`, `OFFICER`, `MEMBER` |
  | Refused change | HTTP 403, `error.html` |
  | Blank WSU ID | HTTP 200, `clubMembers.html` again with the message next to the WSU ID field |

- **Assumptions:**
  - Sign-in (FR-010.1) is merged before this feature, so there is always a signed-in student. A signed-out visitor is sent to the sign-in page first (FR-010-05). Until then the signed-in student is stubbed.
  - `ClubRole` has the values `PRESIDENT`, `OFFICER` and `MEMBER`. Add any that are missing.
  - The only required information for "Add Member" is the WSU ID, and the student must already have a student profile with that WSU ID. A WSU ID that matches no student is reported like a blank one.
  - Until FR-006.1 is merged there is no shared header or `error.html`, so a 403 shows Spring Boot's default error page. Once it is merged, `clubMembers.html` inserts the header like every other page.
  - Every POST form uses `th:action`, so the CSRF token from FR-010 is included.
  - Desktop browsers only; no mobile layout (`mission.md`).
  - Not in this feature: changing a member's role, declining a request, and e-mail notifications.
