# Links

## Moving from @RestController to Spring MVC + Thymeleaf

### Start here
1. [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content) (Spring guide)
   Builds a `@Controller` that passes data through a `Model` to a Thymeleaf page. Closest match to our switch; about 15 minutes.
2. [Handling Form Submission](https://spring.io/guides/gs/handling-form-submission) (Spring guide)
   HTML forms that post data back to a controller, e.g. "create a club" or "join a club".

### The concept
3. [@Controller vs @RestController](https://www.baeldung.com/spring-controller-vs-restcontroller) (Baeldung)
   Short explanation of the difference: JSON responses vs. rendered HTML pages.

### Thymeleaf
4. [Thymeleaf + Spring tutorial](https://www.thymeleaf.org/doc/tutorials/3.1/thymeleafspring.html) (official)
   Reference for Thymeleaf syntax in Spring: `th:text`, `th:each` for lists, `th:object` for forms.
5. [Spring MVC and Thymeleaf](https://www.baeldung.com/thymeleaf-in-spring-mvc) (Baeldung)
   Friendlier than the official docs, with lots of small examples.

### Later
6. [Validating Form Input](https://spring.io/guides/gs/validating-form-input) (Spring guide)
   Form rules like "email is required". Needs the Validation dependency, which we don't have yet.
7. [Annotated Controllers](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html) (Spring reference)
   Full reference for `@GetMapping`, `@PostMapping`, `@ModelAttribute`, etc. Good for looking things up.

**Suggested first conversion:** `StudentController`. It has one endpoint and a simple DTO.
`/students/{id}` would add the student to a `Model` and return `"student"`, which renders `templates/student.html`.

## Spring Data JPA
- [Building a REST service](https://spring.io/guides/gs/rest-service) (Spring guide)
- [Accessing data with JPA](https://spring.io/guides/gs/accessing-data-jpa) (Spring guide)

## Maven
- [Maven Coordinates explainer](MavenCoordinates.md): groupId, artifactId, version
- [Maven naming conventions](https://maven.apache.org/guides/mini/guide-naming-conventions.html)
