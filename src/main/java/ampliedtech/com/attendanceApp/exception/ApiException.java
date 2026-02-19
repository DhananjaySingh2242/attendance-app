package ampliedtech.com.attendanceApp.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception for API errors. Subclasses define status and client-safe message.
 */
public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    protected ApiException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode != null ? errorCode : "API_ERROR";
    }

    protected ApiException(String message, HttpStatus status) {
        this(message, status, null);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
