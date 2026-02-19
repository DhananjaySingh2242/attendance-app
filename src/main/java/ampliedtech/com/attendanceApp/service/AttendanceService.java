package ampliedtech.com.attendanceApp.service;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;


public interface AttendanceService {

    void checkIn(@AuthenticationPrincipal Jwt jwt);

    void checkOut(@AuthenticationPrincipal Jwt jwt);
}
