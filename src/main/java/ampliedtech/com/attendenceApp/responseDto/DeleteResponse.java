package ampliedtech.com.attendenceApp.responseDto;

import java.time.LocalDateTime;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteResponse {
private Long userId;
private String message;
private LocalDateTime timeStamp;
}