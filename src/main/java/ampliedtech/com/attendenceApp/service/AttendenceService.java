package ampliedtech.com.attendenceApp.service;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;


public interface AttendenceService {

    void checkIn(@AuthenticationPrincipal Jwt jwt);

    void checkOut(@AuthenticationPrincipal Jwt jwt);
}
