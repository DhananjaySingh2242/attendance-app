package ampliedtech.com.attendenceApp.responseDto;

import java.time.LocalDate;

import ampliedtech.com.attendenceApp.entity.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class AttendanceResponse {
    private String keycloakId;
    private String email;
    private LocalDate date;
    private AttendanceStatus status;
}