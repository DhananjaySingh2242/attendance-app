package ampliedtech.com.attendenceApp.responseDto;

import ampliedtech.com.attendenceApp.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String name;
   private Role role;
}