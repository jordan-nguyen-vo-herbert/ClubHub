package com.clubhub.student;

import com.clubhub.repository.StudentRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// TEST-004 (FR-001), second half: a WSU ID that isn't on record shows the 404 page.
// MockMvc stops at the 404 status without running Spring's error dispatch, so this one starts a
// real server and asks for HTML, the same way a browser would.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("TEST-004: Missing student profile shows the 404 page (FR-001)")
class StudentProfileNotFoundTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private StudentRepository studentRepository;

    @Test
    @DisplayName("An id with no student behind it returns 404 and renders error/404.html")
    void unknownStudentIdShowsNotFoundPage() {
        long missingId = 999_999L;
        // Make sure the id really is unused, otherwise this test proves nothing
        assertThat(studentRepository.findById(missingId)).isEmpty();

        ResponseEntity<String> response = getAsBrowser("/students/" + missingId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        // The custom page, not a stack trace or the default Whitelabel error page
        assertThat(response.getBody()).contains("Page not found");
        assertThat(response.getBody()).contains("We couldn't find what you were looking for.");
        assertThat(response.getBody()).doesNotContain("Whitelabel Error Page");
    }

    @Test
    @DisplayName("A non-numeric id is also a 404, not a server error")
    void nonNumericStudentIdIsNotAServerError() {
        // /students/{id} binds to a Long, "abcd" can't convert
        ResponseEntity<String> response = getAsBrowser("/students/abcd");

        assertThat(response.getStatusCode().is5xxServerError()).isFalse();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // TestRestTemplate asks for JSON by default, which gets the JSON error body instead of the page
    private ResponseEntity<String> getAsBrowser(String path) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.TEXT_HTML));
        return restTemplate.exchange("http://localhost:" + port + path,
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }
}
