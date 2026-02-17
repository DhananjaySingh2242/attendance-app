package ampliedtech.com.attendenceApp.document;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "attendanceLog")
public class AttendanceLog {
    @Id
    private String id;
    private String userId;
    private String email;
    private String action;
    private LocalDateTime time;

    public void setUserId(String userId) { this.userId = userId; }
    public void setEmail(String email) { this.email = email; }
    public void setAction(String action) { this.action = action; }
    public void setTime(LocalDateTime time) { this.time = time; }
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getAction() { return action; }
    public LocalDateTime getTime() { return time; }
}