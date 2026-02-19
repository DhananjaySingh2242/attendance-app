package ampliedtech.com.attendanceApp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  @Column(unique = true)
  private String keycloakId;
  @Column(unique = true)
  private String email;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, columnDefinition = "varchar(20)")
  private Role role;

  // Explicit accessors for when Lombok annotation processing is unavailable (e.g. certain JDK versions)
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getKeycloakId() { return keycloakId; }
  public void setKeycloakId(String keycloakId) { this.keycloakId = keycloakId; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public Role getRole() { return role; }
  public void setRole(Role role) { this.role = role; }

  public static UserBuilder builder() {
    return new UserBuilder();
  }

  public static final class UserBuilder {
    private Long id;
    private String name;
    private String keycloakId;
    private String email;
    private Role role;

    UserBuilder() {}

    public UserBuilder id(Long id) { this.id = id; return this; }
    public UserBuilder name(String name) { this.name = name; return this; }
    public UserBuilder keycloakId(String keycloakId) { this.keycloakId = keycloakId; return this; }
    public UserBuilder email(String email) { this.email = email; return this; }
    public UserBuilder role(Role role) { this.role = role; return this; }

    public User build() {
      User user = new User();
      user.setId(id);
      user.setName(name);
      user.setKeycloakId(keycloakId);
      user.setEmail(email);
      user.setRole(role);
      return user;
    }
  }
}