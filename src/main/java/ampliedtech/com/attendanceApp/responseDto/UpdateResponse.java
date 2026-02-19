package ampliedtech.com.attendanceApp.responseDto;

public class UpdateResponse {
    private Long id;
    private String name;
    private String email;
    private String message;
    private String password;

    public void setMessage(String message) { this.message = message; }
    public String getMessage() { return message; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
