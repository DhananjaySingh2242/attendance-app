package ampliedtech.com.attendenceApp.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import ampliedtech.com.attendenceApp.service.AttendenceService;

@RestController
@RequestMapping("/attendance")
public class AttendenceController {
    private static final Logger log = LoggerFactory.getLogger(AttendenceController.class);

    private final AttendenceService attendenceService;

    @Autowired
    public AttendenceController(AttendenceService attendanceService) {
        this.attendenceService = attendanceService;
    }

    @PostMapping("/check-In")
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
        log.info("Check-out succesfull");
        return ResponseEntity.ok("Checked out successfully");
    }
}