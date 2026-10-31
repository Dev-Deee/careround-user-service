package com.dayo.careround.userservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void unmappedPathIs404WithTheMethodAndPath() {
        ResponseEntity<Map<String, Object>> r = handler.handleNoResource(new NoResourceFoundException(HttpMethod.GET, "/api/v1/users/", "api/v1/users"));
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(r.getBody()).containsEntry("status", 404).containsEntry("error", "Not Found")
                .containsEntry("message", "No endpoint GET /api/v1/users").containsKey("timestamp");
    }

    @Test
    void wrongMethodIs405() {
        ResponseEntity<Map<String, Object>> r = handler.handleMethodNotSupported(new HttpRequestMethodNotSupportedException("DELETE"));
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    void nonUuidIdIs400() throws Exception {
        MethodParameter param = new MethodParameter(GlobalExceptionHandlerTest.class.getDeclaredMethod("sample", UUID.class), 0);
        ResponseEntity<Map<String, Object>> r = handler.handleTypeMismatch(
                new MethodArgumentTypeMismatchException("not-a-uuid", UUID.class, "publicId", param, new IllegalArgumentException()));
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(r.getBody()).containsEntry("message", "Invalid value for parameter 'publicId': 'not-a-uuid'");
    }

    @Test
    void unexpectedErrorIsGeneric500() {
        ResponseEntity<Map<String, Object>> r = handler.handleGeneral(new IllegalStateException("internal detail"));
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(r.getBody()).containsEntry("message", "An unexpected error occurred");
        assertThat(r.getBody().toString()).doesNotContain("internal detail");
    }

    @SuppressWarnings("unused")
    private void sample(UUID publicId) { }
}
