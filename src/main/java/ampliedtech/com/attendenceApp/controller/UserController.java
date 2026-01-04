package ampliedtech.com.attendenceApp.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ampliedtech.com.attendenceApp.entity.User;
import ampliedtech.com.attendenceApp.dto.RegisterRequest;
import ampliedtech.com.attendenceApp.dto.LoginRequest;
import ampliedtech.com.attendenceApp.dto.AuthResponse;
import ampliedtech.com.attendenceApp.service.UserService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {
        log.info("Register API called for email: {}", request.getEmail());
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        log.info("Login API called for email: {}", request.getEmail());
        return ResponseEntity.ok(userService.loginUser(request));
    }

    @GetMapping("/me")
    public ResponseEntity<PagedModel<User>> getCurrentUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
                Page<User> userPage= userService.getCurrentUser(page, size);
        return ResponseEntity.ok(new PagedModel<>(userPage));
    }
}