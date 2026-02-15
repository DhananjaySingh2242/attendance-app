package ampliedtech.com.attendenceApp.document;

import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSession {
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Long durationMinutes;
}
