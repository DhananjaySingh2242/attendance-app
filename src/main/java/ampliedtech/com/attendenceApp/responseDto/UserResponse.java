package ampliedtech.com.attendenceApp.responseDto;

import java.util.List;

public class UserResponse {
    private String keycloakId;
    private String email;
    private String name;
    private List<String> role;

    public UserResponse() {}
    public UserResponse(String keycloakId, String email, String name, List<String> role) {
        this.keycloakId = keycloakId;
        this.email = email;
        this.name = name;
        this.role = role;
    }
    public String getKeycloakId() { return keycloakId; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public List<String> getRole() { return role; }
}