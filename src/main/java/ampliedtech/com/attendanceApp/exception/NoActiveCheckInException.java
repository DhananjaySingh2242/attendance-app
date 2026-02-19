package ampliedtech.com.attendanceApp.exception;

import org.springframework.http.HttpStatus;

public class NoActiveCheckInException extends ApiException {

    public NoActiveCheckInException() {
        super("No active check-in found", HttpStatus.BAD_REQUEST, "NO_ACTIVE_CHECK_IN");
    }
}
