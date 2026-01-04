package ampliedtech.com.attendenceApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @Size(min = 4, message = "Password must be at least 4 characters")
    @NotBlank(message = "Password is required")
    private String password;
}
