package ampliedtech.com.attendanceApp.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @Size(min = 4, message = "Password must be at least 4 characters")
    @NotBlank(message = "Password is required")
    private String password;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
