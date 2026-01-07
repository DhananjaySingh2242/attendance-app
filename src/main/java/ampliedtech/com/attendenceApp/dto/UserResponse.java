package ampliedtech.com.attendenceApp.dto;

import ampliedtech.com.attendenceApp.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String name;
   private Role role;
}