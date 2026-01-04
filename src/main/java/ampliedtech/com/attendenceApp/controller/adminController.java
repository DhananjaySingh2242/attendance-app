package ampliedtech.com.attendenceApp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ampliedtech.com.attendenceApp.dto.UpdateRequest;
import ampliedtech.com.attendenceApp.entity.User;
import ampliedtech.com.attendenceApp.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/admin")
public class adminController {
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(adminController.class);

    public adminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all-user")
    public ResponseEntity<PagedModel<User>> getAllUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching all users - Page: {}, Size: {}", page, size);
        try {
            Page<User> user = userService.getAllUser(page, size);
            return ResponseEntity.ok(new PagedModel<>(user));
        } catch (Exception ex) {
            log.error("Database error during user fetch");
            throw new RuntimeException("Could not retrive user list for page " + page, ex);
        }
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUser(
            @PathVariable @Positive(message = "User id must be greater than 0") Long id) {
        log.warn("Delete user API called for userId: {}", id);
        return userService.deleteUser(id);
    }

    @PatchMapping("/update/{id}")
    public User updateUser(
            @PathVariable @Positive(message = "User id must be greater than 0") Long id,
            @Valid @RequestBody UpdateRequest request) {

        log.info("Update user API called for userId: {}", id);
        return userService.updateUser(id, request);
    }
}
