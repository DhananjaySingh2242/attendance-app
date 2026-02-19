package ampliedtech.com.attendanceApp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ampliedtech.com.attendanceApp.responseDto.AllAtendanceResponse;
import ampliedtech.com.attendanceApp.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        userService.ensureUserExists(jwt);
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        Object roles = (realmAccess != null && realmAccess.containsKey("roles"))
                ? realmAccess.get("roles")
                : Collections.emptyList();
        return Map.of(
                "userId", jwt.getSubject() != null ? jwt.getSubject() : "",
                "username", jwt.getClaimAsString("preferred_username") != null ? jwt.getClaimAsString("preferred_username") : "",
                "email", jwt.getClaimAsString("email") != null ? jwt.getClaimAsString("email") : "",
                "name", jwt.getClaimAsString("name") != null ? jwt.getClaimAsString("name") : "",
                "roles", roles);
    }

    @GetMapping("/my-attendance")
    public ResponseEntity<List<AllAtendanceResponse>> currentUserAttendacne() {
        return ResponseEntity.ok(userService.currentUserAllAttendance());
    }

}