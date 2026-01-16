package ampliedtech.com.attendenceApp.event;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttendanceEvent implements Serializable {
    private Long id;
    private String email;
    private String action;
    private LocalDateTime time;
}
