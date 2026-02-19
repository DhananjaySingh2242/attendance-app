package ampliedtech.com.attendanceApp.responseDto;

import java.util.List;

public class UserResponse {
    private Long id;
    private String keycloakId;
    private String email;
    private String name;
    private List<String> role;

    public UserResponse() {}
    public UserResponse(Long id, String keycloakId, String email, String name, List<String> role) {
        this.id = id;
        this.keycloakId = keycloakId;
        this.email = email;
        this.name = name;
        this.role = role;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getKeycloakId() { return keycloakId; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public List<String> getRole() { return role; }
}