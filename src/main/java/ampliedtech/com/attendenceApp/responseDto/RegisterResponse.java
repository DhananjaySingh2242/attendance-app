package ampliedtech.com.attendenceApp.responseDto;

import java.time.LocalDateTime;

public class RegisterResponse {
    private Long userId;
    private String message;
    private LocalDateTime timestamp;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
