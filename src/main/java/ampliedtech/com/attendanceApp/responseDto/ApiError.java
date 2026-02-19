package ampliedtech.com.attendanceApp.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

/**
 * Standard API error response body. Used by GlobalExceptionHandler.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private final String errorCode;
    private final String message;
    private final int status;
    private final Instant timestamp;
    private final Map<String, String> details;

    public ApiError(String errorCode, String message, int status, Map<String, String> details) {
        this.errorCode = errorCode;
        this.message = message;
        this.status = status;
        this.timestamp = Instant.now();
        this.details = details;
    }

    public static ApiError of(String errorCode, String message, int status) {
        return new ApiError(errorCode, message, status, null);
    }

    public static ApiError of(String errorCode, String message, int status, Map<String, String> details) {
        return new ApiError(errorCode, message, status, details);
    }

    public String getErrorCode() { return errorCode; }
    public String getMessage() { return message; }
    public int getStatus() { return status; }
    public Instant getTimestamp() { return timestamp; }
    public Map<String, String> getDetails() { return details; }
}
