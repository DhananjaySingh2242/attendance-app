package ampliedtech.com.attendanceApp.gExceptionHandler;

import ampliedtech.com.attendanceApp.exception.ApiException;
import ampliedtech.com.attendanceApp.responseDto.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String GENERIC_ERROR_MESSAGE = "An unexpected error occurred. Please try again later.";

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException ex) {
        log.warn("API exception: {} [{}]", ex.getMessage(), ex.getErrorCode());
        ApiError body = ApiError.of(
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getStatus().value()
        );
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> details = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : "Invalid",
                        (a, b) -> a
                ));
        log.debug("Validation failed: {}", details);
        ApiError body = ApiError.of("VALIDATION_ERROR", "Validation failed", HttpStatus.BAD_REQUEST.value(), details);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        ApiError body = ApiError.of("ACCESS_DENIED", "Access denied", HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ApiError> handleRestClientError(RestClientResponseException ex) {
        log.warn("External service error (Keycloak): {} - {}", ex.getStatusCode(), ex.getMessage());
        String msg = "Keycloak returned " + ex.getStatusCode().value() + ". Check KEYCLOAK_ADMIN_CLIENT_SECRET and Keycloak setup.";
        ApiError body = ApiError.of("KEYCLOAK_ERROR", msg, HttpStatus.BAD_GATEWAY.value());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

    /** Avoid 500 when browser requests missing static files (e.g. favicon.ico). */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFound(NoResourceFoundException ex) {
        String path = ex.getResourcePath() != null ? ex.getResourcePath() : "";
        if (path.equals("favicon.ico") || path.endsWith("/favicon.ico")) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex) {
        log.error("Unhandled exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        ApiError body = ApiError.of("INTERNAL_ERROR", GENERIC_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
