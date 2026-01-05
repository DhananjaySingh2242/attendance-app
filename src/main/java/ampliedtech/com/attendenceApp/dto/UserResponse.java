package ampliedtech.com.attendenceApp.dto;

import java.util.List;

import ampliedtech.com.attendenceApp.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String name;
   private Role role;
}
