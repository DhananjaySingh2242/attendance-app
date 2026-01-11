package ampliedtech.com.attendenceApp.responseDto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DeleteResponse {
private Long userId;
private String message;
private LocalDateTime timeStamp;
}
