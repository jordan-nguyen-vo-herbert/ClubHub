# Technology Stack

ClubHub is a server-rendered web application written in Java. Spring Boot handles each request, Spring Data JPA stores the data, and Thymeleaf renders the page the student sees. All of the code lives in `code/backend/`. The `code/frontend/` folder is not used, because the backend serves the Thymeleaf pages.

## Frontend
**Thymeleaf 3.1 templates with HTML and CSS**, rendered on the server by Spring Boot.

- **Why:** Thymeleaf is part of the Java and Spring ecosystem, so the backend renders every page and there is no separate frontend application or REST client to build. That suits a small team with a well-defined scope.
- **Why:** Its Spring Security integration works with the server-side session, so there are no tokens to manage, and `sec:authorize` shows or hides content by role. Sign-in (FR-010) uses it in Sprint 2 and roles (FR-009) in Sprint 3.
- **Where:** pages are in `code/backend/src/main/resources/templates/`, with shared pieces in `fragments/` and error pages in `error/`. Styles are in `static/styles.css`.

## Backend
**Java 17 and Spring Boot 3.2**, with Spring Web MVC and Spring Data JPA (Hibernate), built with Maven through the wrapper (`./mvnw`).

- **Why:** Spring Boot maps each URL and HTTP method to a controller method: a GET request loads a page or form, and a POST request submits it. Through inversion of control, Spring creates and connects the controllers and repositories from their annotations, so the team writes feature logic instead of setup code.
- **Structure:** in `code/backend/src/main/java/com/clubhub/`, entities in `entities/` define the data, repositories in `repository/` read and write it, and controllers in `controller/` handle requests and choose a template. In `dto/`, form classes hold what a form submits, and response classes copy only the fields a page may show, so a password never reaches a template.
- **Sign-in (Sprint 2):** Spring Security 6.2 handles sign-in, sign-out and redirects to the sign-in page, and passwords are stored as BCrypt hashes (FR-010.1).

## Database
**PostgreSQL** for the running application, and **H2 2.2** in memory for tests.

- **Why PostgreSQL:** it keeps data in a managed data directory that survives restarts, handles many users at once, and provides network access and user management. Spring Boot can group inserts and updates into batches to cut round trips, and PostgreSQL's `COPY` command loads CSV or text data in bulk.
- **Why H2 for tests:** it runs in memory and starts empty on every run, so tests need no database install and never touch real data.
- All data access goes through Spring Data JPA, so moving from H2 to PostgreSQL is a configuration change, not a rewrite.
- Until DB-1 (#54) is merged, the application itself also runs on H2, and its data is lost on restart.

### Local Setup
DB-1 (#54) fills in this section when PostgreSQL is merged:

- The PostgreSQL version and how to install it
- How to create the database and its user
- Which settings go in `application.properties` and which are supplied on each machine

Never commit a real password. Keep credentials out of the repository, for example in environment variables. Tests keep running on H2, so they need no PostgreSQL install.

## Testing
**JUnit 5** through `spring-boot-starter-test`, which also includes MockMvc, AssertJ, Hamcrest and Mockito. Run every test with `./mvnw test` from `code/backend/`.

- **How tests are written:** `@SpringBootTest` with `@AutoConfigureMockMvc` sends requests to the controllers through MockMvc and checks the response and the saved data. `@Transactional` rolls back each test's changes.
- **Who writes them:** the developer who builds a layer writes white-box tests for it. Specification-based tests are written from each feature's `validation.md`.
- **Naming:** test class names must end in `Test` or `Tests`. Maven Surefire skips classes named any other way, which is why TEST-010 renames `ClubSearchByName` and `ClubSearchByArea`.

## Constraints
Spring Boot 3.2.0 is the parent in `code/backend/pom.xml` and sets the versions below. Don't add a `<version>` to a dependency that Spring Boot manages. To upgrade, change the parent version and rerun the tests.

| Component | Version |
|---|---|
| Java language level | 17 |
| Spring Boot | 3.2.0 |
| Spring Framework | 6.1.1 |
| Spring Data JPA | 3.2 (Spring Data 2023.1.0) |
| Hibernate ORM | 6.3.1.Final |
| Thymeleaf | 3.1.2 |
| Spring Security (FR-010, Sprint 2) | 6.2.0 |
| Thymeleaf Spring Security extras (Sprint 2) | 3.1.2 |
| H2 | 2.2.224 |
| PostgreSQL JDBC driver (DB-1, Sprint 2) | 42.6.0 |
| JUnit Jupiter | 5.10.1 |
| Embedded Tomcat | 10.1.16 |
| Maven, through the wrapper | 3.9.16 |

- **Java 17:** the code targets Java 17 (`<java.version>17</java.version>`), so don't use language features newer than Java 17.
- **Build:** use the Maven wrapper from `code/backend/` (`./mvnw` on macOS and Linux, `mvnw.cmd` on Windows), so everyone builds with the same Maven version.
- **Languages:** Java, plus HTML and CSS in the Thymeleaf templates. There is no JavaScript framework.
- **Sign-in:** real WSU single sign-on is out of scope. Students sign in with the WSU email and password stored in ClubHub (FR-010).
- **Sessions:** the session ID is kept in a cookie only (`server.servlet.session.tracking-modes=cookie`), so it never appears in a URL.
- **Secrets:** never commit a real password or mail account credential.
- **Email (Sprint 3):** the FR-005 spec decides whether notifications go through a real mail account or a local test mail server (FR-005.2).
- **Tools:** VS Code is the team's IDE. GitHub hosts the code, and the GitHub Projects Kanban board tracks every item.
- **Workflow:** every change goes through a pull request that another team member reviews before it is merged into `main`.
- **AI assistance:** Claude Code and Copilot can explain the tools and, once the team understands a feature's design, generate parts of it. The author must be able to review, explain and change any generated code.
