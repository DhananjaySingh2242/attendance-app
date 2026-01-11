package ampliedtech.com.attendenceApp.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ampliedtech.com.attendenceApp.document.AttendanceDocument;
import ampliedtech.com.attendenceApp.document.AttendanceLog;
import ampliedtech.com.attendenceApp.document.AttendanceSession;
import ampliedtech.com.attendenceApp.entity.AttendanceStatus;
import ampliedtech.com.attendenceApp.entity.User;
import ampliedtech.com.attendenceApp.repository.AttendanceLogRepository;
import ampliedtech.com.attendenceApp.repository.AttendanceRepo;
import ampliedtech.com.attendenceApp.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class AttendanceServiceImpl implements AttendenceService {
    private static final Logger log = LoggerFactory.getLogger(AttendanceServiceImpl.class);
    private final UserRepository userRepository;
    private final AttendanceLogRepository attendanceLogRepository;
    private final AttendanceRepo attendanceRepo;
    private final StringRedisTemplate redisTemplate;

    public AttendanceServiceImpl(AttendanceRepo attendanceRepo, UserRepository userRepository,
            AttendanceLogRepository attendanceLogRepository, StringRedisTemplate redisTemplate) {
        this.attendanceRepo = attendanceRepo;
        this.userRepository = userRepository;
        this.attendanceLogRepository = attendanceLogRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public void checkIn(String email) {

        log.info("Check-in attempt for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        String redisKey = "attendance:active:" + user.getId();

        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            throw new RuntimeException("Already checked in");
        }

        LocalDateTime now = LocalDateTime.now();

        long ttl = getMillisTillMidnight();
        if (ttl <= 0) {
            ttl = 5 * 60 * 1000;
        }

        redisTemplate.opsForValue().set(
                redisKey,
                now.toString(),
                ttl,
                TimeUnit.MILLISECONDS);

        log.info("Redis key {} saved with TTL {}", redisKey, ttl);

        LocalDate today = LocalDate.now();

        AttendanceDocument attendance = attendanceRepo
                .findByEmailAndDate(user.getEmail(), today)
                .orElse(
                        AttendanceDocument.builder()
                                .userId(user.getId())
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
        saveLog(user, "CHECK_IN");

        log.info("Check-in successful for {}", email);
    }

    @Override
    @Transactional
    public void checkOut(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String redisKey = "attendance:active:" + user.getId();

        String checkInTimeStr = redisTemplate.opsForValue().get(redisKey);
        if (checkInTimeStr == null) {
            throw new RuntimeException("No active check-in found");
        }

        LocalDateTime checkInTime = LocalDateTime.parse(checkInTimeStr);
        LocalDateTime checkOutTime = LocalDateTime.now();

        AttendanceDocument attendance = attendanceRepo
                .findByEmailAndDate(user.getEmail(), LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Attendance not found"));

        AttendanceSession lastSession = attendance.getSessions().get(attendance.getSessions().size() - 1);

        lastSession.setCheckOut(checkOutTime);

        long minutes = Duration.between(checkInTime, checkOutTime).toMinutes();
        lastSession.setDurationMinutes(minutes);

        attendance.setTotalDurationMinutes(
                attendance.getTotalDurationMinutes() + minutes);

        attendance.setStatus(calculateStatus(attendance.getTotalDurationMinutes()));
        attendanceRepo.save(attendance);

        redisTemplate.delete(redisKey);

        saveLog(user, "CHECK_OUT");
        log.info("Check-out successful for {}", email);
    }

    private AttendanceStatus calculateStatus(long totalMinutes) {
        if (totalMinutes < 120)
            return AttendanceStatus.ABSENT;
        if (totalMinutes <= 240)
            return AttendanceStatus.HALFDAY;
        return AttendanceStatus.PRESENT;
    }

    private void saveLog(User user, String action) {
        AttendanceLog logg = new AttendanceLog();
        logg.setUserId(user.getId());
        logg.setEmail(user.getEmail());
        logg.setAction(action);
        logg.setTime(LocalDateTime.now());
        attendanceLogRepository.save(logg);
    }

    private long getMillisTillMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
        return Duration.between(now, midnight).toMillis();
    }

}