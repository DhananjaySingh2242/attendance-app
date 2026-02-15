package ampliedtech.com.attendenceApp.responseDto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private String keycloakId;
    private String email;
    private String name;
    private List<String> role;
}