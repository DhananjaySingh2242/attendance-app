package ampliedtech.com.attendenceApp.document;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Document(collection = "attendanceLog")
@Data
public class AttendanceLog {
    @Id
    private String id;
    private Long userId;
    private String email;
    private String action;
    private LocalDateTime time;
}
