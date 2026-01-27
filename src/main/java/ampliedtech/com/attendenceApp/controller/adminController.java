package ampliedtech.com.attendenceApp.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ampliedtech.com.attendenceApp.entity.User;
import ampliedtech.com.attendenceApp.requestDto.RegisterRequest;
import ampliedtech.com.attendenceApp.requestDto.UpdateRequest;
import ampliedtech.com.attendenceApp.responseDto.AttendanceResponse;
import ampliedtech.com.attendenceApp.responseDto.DeleteResponse;
import ampliedtech.com.attendenceApp.responseDto.RegisterResponse;
import ampliedtech.com.attendenceApp.responseDto.UpdateResponse;
import ampliedtech.com.attendenceApp.responseDto.UserResponse;
import ampliedtech.com.attendenceApp.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        log.info("Register API called for email: {}", request.getEmail());
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @GetMapping("/all-users")
    public ResponseEntity<Page<UserResponse>> getAllUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching all users - Page: {}, Size: {}", page, size);
        try {
            Page<UserResponse> dtoPage = userService.getAllUser(page, size);
            return ResponseEntity.ok(dtoPage);
        } catch (Exception ex) {
            log.error("Database error during user fetch");
            throw new RuntimeException("Could not retrieve user list for page " + page, ex);
        }
    }

    @DeleteMapping("/delete/{id}")
    public DeleteResponse deleteUser(
            @PathVariable @Positive(message = "User id must be greater than 0") Long id) {
        log.warn("Delete user API called for userId: {}", id);
        return userService.deleteUser(id);
    }

    @PatchMapping("/update/{id}")
    public UpdateResponse updateUser(
            @PathVariable @Positive(message = "User id must be greater than 0") Long id,
            @Valid @RequestBody UpdateRequest request) {

        log.info("Update user API called for userId: {}", id);
        return userService.updateUser(id, request);
    }

    @GetMapping("/all-attendance")
    public ResponseEntity<Page<AttendanceResponse>> getAttendance(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching all users - Page: {}, Size: {}", page, size);
        try {
            Page<AttendanceResponse> response = userService.getAttendance(page, size);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            log.error("Database Error during fetch");
            throw new RuntimeException("Could not retrieve user list for page " + page, ex);
        }
    }
    @GetMapping("/users/search")
    public List<UserResponse> searchUsers(@RequestParam String keyword){
        return userService.searchUsers(keyword);
    }
}