package ampliedtech.com.attendanceApp.responseDto;

import java.time.LocalDate;

import ampliedtech.com.attendanceApp.entity.AttendanceStatus;

public class AttendanceResponse {
    private String keycloakId;
    private String email;
    private LocalDate date;
    private AttendanceStatus status;

    public AttendanceResponse() {}
    public AttendanceResponse(String keycloakId, String email, LocalDate date, AttendanceStatus status) {
        this.keycloakId = keycloakId;
        this.email = email;
        this.date = date;
        this.status = status;
    }
    public String getKeycloakId() { return keycloakId; }
    public String getEmail() { return email; }
    public LocalDate getDate() { return date; }
    public AttendanceStatus getStatus() { return status; }
}