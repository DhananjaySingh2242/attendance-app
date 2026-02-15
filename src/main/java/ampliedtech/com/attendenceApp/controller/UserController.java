package ampliedtech.com.attendenceApp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampliedtech.com.attendenceApp.responseDto.AllAtendanceResponse;
import ampliedtech.com.attendenceApp.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        String keycloakUserId = jwt.getSubject();
        userService.ensureUserExists(jwt);
        return Map.of(
                "userId", jwt.getSubject(),
                "username", jwt.getClaimAsString("preferred_username"),
                "email", jwt.getClaimAsString("email"),
                "name", jwt.getClaimAsString("name"),
                "roles", jwt.getClaimAsMap("realm_access").get("roles"));
    }

    @GetMapping("/my-attendance")
    public ResponseEntity<List<AllAtendanceResponse>> currentUserAttendacne() {
        return ResponseEntity.ok(userService.currentUserAllAttendance());
    }

}