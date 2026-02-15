package ampliedtech.com.attendenceApp.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ampliedtech.com.attendenceApp.document.AttendanceDocument;
import ampliedtech.com.attendenceApp.document.AttendanceSession;
import ampliedtech.com.attendenceApp.entity.AttendanceStatus;
import ampliedtech.com.attendenceApp.entity.Role;
import ampliedtech.com.attendenceApp.entity.User;
import ampliedtech.com.attendenceApp.event.AttendanceEvent;
import ampliedtech.com.attendenceApp.jpaRepo.UserRepository;
import ampliedtech.com.attendenceApp.mongoRepo.AttendanceRepo;
import ampliedtech.com.attendenceApp.publisher.AttendanceEventPublisher;
import ampliedtech.com.attendenceApp.utils.CountMillisTillMidNight;
import ampliedtech.com.attendenceApp.utils.ValidUserRole;

import org.springframework.security.oauth2.jwt.Jwt;

import jakarta.transaction.Transactional;

@Service
public class AttendanceServiceImpl implements AttendenceService {
    private static final Logger log = LoggerFactory.getLogger(AttendanceServiceImpl.class);
    private final UserRepository userRepository;
    private final AttendanceRepo attendanceRepo;
    private final StringRedisTemplate redisTemplate;
    private final AttendanceEventPublisher attendanceEventPublisher;
    private final ValidUserRole validUserRole;

    public AttendanceServiceImpl(AttendanceRepo attendanceRepo, UserRepository userRepository,
            StringRedisTemplate redisTemplate,
            AttendanceEventPublisher attendanceEventPublisher,
            ValidUserRole validUserRole) {
        this.attendanceRepo = attendanceRepo;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.attendanceEventPublisher = attendanceEventPublisher;
        this.validUserRole = validUserRole;
    }

    @Override
    @Transactional
    public void checkIn(@AuthenticationPrincipal Jwt jwt) {
        validUserRole.validateUserRole(jwt);

        String keycloakId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");

        // Extract role from JWT and convert to enum
        String roleStr = validUserRole.extractPrimaryRole(jwt);
        final Role role = safeRole(roleStr);

        // Ensure user exists in DB
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    User u = new User();
                    u.setKeycloakId(keycloakId);
                    u.setEmail(email);
                    u.setRole(role);
                    return userRepository.save(u);
                });

        String redisKey = "attendance:active:" + user.getKeycloakId();

        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            throw new RuntimeException("Already checked in");
        }

        LocalDateTime now = LocalDateTime.now();
        long ttl = CountMillisTillMidNight.getMillisTillMidNight();
        if (ttl <= 0) {
            ttl = 60 * 60 * 1000;
        }

        redisTemplate.opsForValue().set(
                redisKey,
                now.toString(),
                ttl,
                TimeUnit.MILLISECONDS);

        log.info("Redis key {} saved with TTL {}", redisKey, ttl);

        LocalDate today = LocalDate.now();

        AttendanceDocument attendance = attendanceRepo
                .findByKeycloakIdAndDate(keycloakId, today)
                .orElse(
                        AttendanceDocument.builder()
                                .keycloakId(user.getKeycloakId())
                                .email(user.getEmail())
                                .date(today)
                                .sessions(new ArrayList<>())
                                .totalDurationMinutes(0)
                                .status(AttendanceStatus.PRESENT)
                                .build());

        attendance.getSessions().add(
                AttendanceSession.builder()
                        .checkIn(now)
                        .build());

        attendanceRepo.save(attendance);

        AttendanceEvent event = new AttendanceEvent();
        event.setKeycloakId(user.getKeycloakId());
        event.setEmail(user.getEmail());
        event.setAction("CHECK_IN");
        event.setTime(LocalDateTime.now());

        attendanceEventPublisher.publish(event);
        log.info("Check-in successful for {}", email);
    }

    @Override
    @Transactional
    public void checkOut(@AuthenticationPrincipal Jwt jwt) {
        validUserRole.validateUserRole(jwt);

        String keycloakId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");

        log.info("Check-out attempt for keycloakId={}", keycloakId);

        String redisKey = "attendance:active:" + keycloakId;

        String checkInTimeStr = redisTemplate.opsForValue().get(redisKey);
        if (checkInTimeStr == null) {
            throw new RuntimeException("No active check-in found");
        }

        LocalDateTime checkInTime = LocalDateTime.parse(checkInTimeStr);
        LocalDateTime checkOutTime = LocalDateTime.now();

        AttendanceDocument attendance = attendanceRepo
                .findByKeycloakIdAndDate(keycloakId, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Attendance not found"));

        AttendanceSession lastSession = attendance.getSessions().get(attendance.getSessions().size() - 1);
        lastSession.setCheckOut(checkOutTime);

        long minutes = Duration.between(checkInTime, checkOutTime).toMinutes();
        lastSession.setDurationMinutes(minutes);

        attendance.setTotalDurationMinutes(attendance.getTotalDurationMinutes() + minutes);
        attendance.setStatus(calculateStatus(attendance.getTotalDurationMinutes()));

        attendanceRepo.save(attendance);
        redisTemplate.delete(redisKey);

        AttendanceEvent event = new AttendanceEvent();
        event.setKeycloakId(keycloakId);
        event.setEmail(email);
        event.setAction("CHECK_OUT");
        event.setTime(checkOutTime);

        attendanceEventPublisher.publish(event);
        log.info("Check-out successful for keycloakId={}", keycloakId);
    }

    private AttendanceStatus calculateStatus(long totalMinutes) {
        if (totalMinutes <= 120)
            return AttendanceStatus.ABSENT;
        if (totalMinutes <= 240)
            return AttendanceStatus.HALFDAY;
        return AttendanceStatus.PRESENT;
    }

    private Role safeRole(String roleStr) {
        try {
            return Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            return Role.ROLE_USER;
        }
    }
}
