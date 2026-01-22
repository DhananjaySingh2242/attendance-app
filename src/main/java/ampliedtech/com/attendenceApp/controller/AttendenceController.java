package ampliedtech.com.attendenceApp.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import ampliedtech.com.attendenceApp.service.AttendenceService;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "http://localhost:5173")
public class AttendenceController {
    private static final Logger log = LoggerFactory.getLogger(AttendenceController.class);

    private final AttendenceService attendenceService;

    public AttendenceController(AttendenceService attendenceService) {
        this.attendenceService = attendenceService;
    }

    @PostMapping("/check-in")
    public ResponseEntity<String> checkIn(Authentication authentication) {
        try {
            attendenceService.checkIn(authentication.getName());
            log.info("Check-In API called");
            return ResponseEntity.ok("Check-In successfully");
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Attendance already marked for today!");
        }
    }

    @PostMapping("/check-out")
    public ResponseEntity<String> checkOut(Authentication authentication) {
        attendenceService.checkOut(authentication.getName());
        log.info("Check-out successful");
        return ResponseEntity.ok("Checked out successfully");
    }
}