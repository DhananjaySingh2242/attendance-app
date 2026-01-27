package ampliedtech.com.attendenceApp.responseDto;

import java.time.LocalDate;

import ampliedtech.com.attendenceApp.entity.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AllAtendanceResponse {
    LocalDate date;
   private AttendanceStatus status; 
}
