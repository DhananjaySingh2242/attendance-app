package ampliedtech.com.attendanceApp.responseDto;

import java.time.LocalDate;

import ampliedtech.com.attendanceApp.entity.AttendanceStatus;

public class AllAtendanceResponse {
    private LocalDate date;
    private AttendanceStatus status;

    public AllAtendanceResponse() {}
    public AllAtendanceResponse(LocalDate date, AttendanceStatus status) {
        this.date = date;
        this.status = status;
    }
    public LocalDate getDate() { return date; }
    public AttendanceStatus getStatus() { return status; }
}
