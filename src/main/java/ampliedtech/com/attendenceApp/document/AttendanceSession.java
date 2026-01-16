package ampliedtech.com.attendenceApp.document;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AttendanceSession {
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Long durationMinutes;
}
