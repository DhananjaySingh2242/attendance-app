package ampliedtech.com.attendenceApp.responseDto;

import java.time.LocalDateTime;

public class DeleteResponse {
    private Long userId;
    private String message;
    private LocalDateTime timeStamp;

    public DeleteResponse() {}
    public DeleteResponse(Long userId, String message, LocalDateTime timeStamp) {
        this.userId = userId;
        this.message = message;
        this.timeStamp = timeStamp;
    }
    public Long getUserId() { return userId; }
    public String getMessage() { return message; }
    public LocalDateTime getTimeStamp() { return timeStamp; }
}