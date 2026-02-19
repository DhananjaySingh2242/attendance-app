package ampliedtech.com.attendanceApp.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import ampliedtech.com.attendanceApp.service.AttendanceService;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private static final Logger log = LoggerFactory.getLogger(AttendanceController.class);

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(@AuthenticationPrincipal Jwt jwt) {
        attendanceService.checkIn(jwt);
        log.info("Check-In API called");
        return ResponseEntity.ok("Check-In successfully");
    }

    @PostMapping("/check-out")
    public ResponseEntity<?> checkOut(@AuthenticationPrincipal Jwt jwt) {
        attendanceService.checkOut(jwt);
        log.info("Check-out successful");
        return ResponseEntity.ok("Checked out successfully");
    }
}
