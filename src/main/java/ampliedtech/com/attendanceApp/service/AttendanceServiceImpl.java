package ampliedtech.com.attendanceApp.service;

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

import ampliedtech.com.attendanceApp.constants.AttendanceConstants;
import ampliedtech.com.attendanceApp.document.AttendanceDocument;
import ampliedtech.com.attendanceApp.document.AttendanceSession;
import ampliedtech.com.attendanceApp.entity.AttendanceStatus;
import ampliedtech.com.attendanceApp.entity.User;
import ampliedtech.com.attendanceApp.event.AttendanceEvent;
import ampliedtech.com.attendanceApp.exception.AlreadyCheckedInException;
import ampliedtech.com.attendanceApp.exception.AttendanceNotFoundException;
import ampliedtech.com.attendanceApp.exception.NoActiveCheckInException;
import ampliedtech.com.attendanceApp.mongoRepo.AttendanceRepo;
import ampliedtech.com.attendanceApp.publisher.AttendanceEventPublisher;
import ampliedtech.com.attendanceApp.utils.CountMillisTillMidNight;
import ampliedtech.com.attendanceApp.utils.ValidUserRole;

import org.springframework.security.oauth2.jwt.Jwt;

import jakarta.transaction.Transactional;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    private static final Logger log = LoggerFactory.getLogger(AttendanceServiceImpl.class);
    private final UserService userService;
    private final AttendanceRepo attendanceRepo;
    private final StringRedisTemplate redisTemplate;
    private final AttendanceEventPublisher attendanceEventPublisher;
    private final ValidUserRole validUserRole;

    public AttendanceServiceImpl(UserService userService, AttendanceRepo attendanceRepo,
            StringRedisTemplate redisTemplate,
            AttendanceEventPublisher attendanceEventPublisher,
            ValidUserRole validUserRole) {
        this.userService = userService;
        this.attendanceRepo = attendanceRepo;
        this.redisTemplate = redisTemplate;
        this.attendanceEventPublisher = attendanceEventPublisher;
        this.validUserRole = validUserRole;
    }

    @Override
    @Transactional
    public void checkIn(@AuthenticationPrincipal Jwt jwt) {
        validUserRole.validateUserRole(jwt);

        String keycloakId = jwt.getSubject();
        User user = userService.findOrCreateUserByJwt(jwt);

        String redisKey = AttendanceConstants.REDIS_KEY_ACTIVE_PREFIX + user.getKeycloakId();

        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            throw new AlreadyCheckedInException();
        }

        LocalDateTime now = LocalDateTime.now();
        long ttl = CountMillisTillMidNight.getMillisTillMidNight();
        if (ttl <= 0) {
            ttl = AttendanceConstants.DEFAULT_REDIS_TTL_MS;
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
        log.info("Check-in successful for {}", user.getEmail());
    }

    @Override
    @Transactional
    public void checkOut(@AuthenticationPrincipal Jwt jwt) {
        validUserRole.validateUserRole(jwt);

        String keycloakId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");

        log.info("Check-out attempt for keycloakId={}", keycloakId);

        String redisKey = AttendanceConstants.REDIS_KEY_ACTIVE_PREFIX + keycloakId;

        String checkInTimeStr = redisTemplate.opsForValue().get(redisKey);
        if (checkInTimeStr == null) {
            throw new NoActiveCheckInException();
        }

        LocalDateTime checkInTime = LocalDateTime.parse(checkInTimeStr);
        LocalDateTime checkOutTime = LocalDateTime.now();

        AttendanceDocument attendance = attendanceRepo
                .findByKeycloakIdAndDate(keycloakId, LocalDate.now())
                .orElseThrow(AttendanceNotFoundException::new);

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
        if (totalMinutes <= AttendanceConstants.MINUTES_ABSENT_THRESHOLD) {
            return AttendanceStatus.ABSENT;
        }
        if (totalMinutes <= AttendanceConstants.MINUTES_HALF_DAY_THRESHOLD) {
            return AttendanceStatus.HALFDAY;
        }
        return AttendanceStatus.PRESENT;
    }
}
