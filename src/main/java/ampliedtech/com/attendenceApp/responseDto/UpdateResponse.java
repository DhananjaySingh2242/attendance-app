package ampliedtech.com.attendenceApp.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateResponse {
private Long id;
private String name;
private String email;
private String message;
private String password;
 }
