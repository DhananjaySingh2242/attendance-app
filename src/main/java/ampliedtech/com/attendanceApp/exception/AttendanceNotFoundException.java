package ampliedtech.com.attendanceApp.exception;

import org.springframework.http.HttpStatus;

public class AttendanceNotFoundException extends ApiException {

    public AttendanceNotFoundException() {
        super("Attendance record not found", HttpStatus.NOT_FOUND, "ATTENDANCE_NOT_FOUND");
    }
}
