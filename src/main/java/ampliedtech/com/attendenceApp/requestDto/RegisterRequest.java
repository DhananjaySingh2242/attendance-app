package ampliedtech.com.attendenceApp.requestDto;


import javax.management.relation.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    @Size(min = 4, message = "Password must be at least 4 characters")
    @NotBlank(message = "Password is required")
    private String password;
}
