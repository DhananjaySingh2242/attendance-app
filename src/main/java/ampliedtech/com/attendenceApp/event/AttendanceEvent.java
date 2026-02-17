package ampliedtech.com.attendenceApp.event;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AttendanceEvent implements Serializable {
    private String keycloakId;
    private String email;
    private String action;
    private LocalDateTime time;

    public String getKeycloakId() { return keycloakId; }
    public void setKeycloakId(String keycloakId) { this.keycloakId = keycloakId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }

    public AttendanceEvent() {}
    public AttendanceEvent(String keycloakId, String email, String action, LocalDateTime time) {
        this.keycloakId = keycloakId;
        this.email = email;
        this.action = action;
        this.time = time;
    }
}
