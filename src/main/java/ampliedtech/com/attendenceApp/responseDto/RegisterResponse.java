package ampliedtech.com.attendenceApp.responseDto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class RegisterResponse {
private Long userId;
private String message;
private LocalDateTime timestamp;
}
