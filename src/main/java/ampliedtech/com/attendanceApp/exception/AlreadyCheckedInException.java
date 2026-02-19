package ampliedtech.com.attendanceApp.exception;

import org.springframework.http.HttpStatus;

public class AlreadyCheckedInException extends ApiException {

    public AlreadyCheckedInException() {
        super("Attendance already marked for today", HttpStatus.CONFLICT, "ALREADY_CHECKED_IN");
    }

    public AlreadyCheckedInException(String message) {
        super(message, HttpStatus.CONFLICT, "ALREADY_CHECKED_IN");
    }
}
