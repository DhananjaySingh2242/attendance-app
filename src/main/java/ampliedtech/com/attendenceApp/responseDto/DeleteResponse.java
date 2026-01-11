package ampliedtech.com.attendenceApp.responseDto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeleteResponse {
private Long userId;
private String message;
private LocalDateTime timeStamp;
}