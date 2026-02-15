package ampliedtech.com.attendenceApp.event;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceEvent implements Serializable {
    private String keycloakId;
    private String email;
    private String action;
    private LocalDateTime time;
}
