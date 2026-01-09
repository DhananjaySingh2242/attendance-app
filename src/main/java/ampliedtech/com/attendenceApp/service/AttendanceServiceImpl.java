package ampliedtech.com.attendenceApp.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ampliedtech.com.attendenceApp.document.AttendanceDocument;
import ampliedtech.com.attendenceApp.document.AttendanceLog;
import ampliedtech.com.attendenceApp.entity.AttendanceStatus;
import ampliedtech.com.attendenceApp.entity.User;
import ampliedtech.com.attendenceApp.repository.AttendanceLogRepository;
import ampliedtech.com.attendenceApp.repository.AttendanceRepo;
import ampliedtech.com.attendenceApp.repository.UserRepository;

@Service
public class AttendanceServiceImpl implements AttendenceService {
    private static final Logger log = LoggerFactory.getLogger(AttendanceServiceImpl.class);
    private final UserRepository userRepository;
    private final AttendanceLogRepository attendanceLogRepository;
    private final AttendanceRepo attendanceRepo;

    public AttendanceServiceImpl(AttendanceRepo attendanceRepo, UserRepository userRepository,
            AttendanceLogRepository attendanceLogRepository) {
        this.attendanceRepo = attendanceRepo;
        this.userRepository = userRepository;
        this.attendanceLogRepository = attendanceLogRepository;
    }

    @Override
    public void checkIn(String email) {
        log.info("Check-in attempt for user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Check-in failed. User not found: {}", email);
                    return new UsernameNotFoundException("User Not Found");
                });

        AttendanceDocument attendance = new AttendanceDocument();
        attendance.setUserId(user.getId());
        attendance.setEmail(user.getEmail());
        attendance.setDate(LocalDate.now());
        attendance.setCheckIn(LocalTime.now());
        attendance.setStatus(AttendanceStatus.PRESENT);
        
        attendanceRepo.save(attendance);
        
        AttendanceLog logg = new AttendanceLog();
        logg.setUserId(user.getId());
        logg.setEmail(email);
        logg.setAction("Check-In");
        logg.setTime(LocalDateTime.now());

        attendanceLogRepository.save(logg);

    }

    @Override
    public void checkOut(String email) {
        log.info("Check-out attempt for user: {}", email);
        AttendanceDocument attendance = attendanceRepo
                .findByEmailAndDate(email, LocalDate.now())
                .orElseThrow(() -> {
                    log.warn("Check-out failed. User not found: {}", email);
                    return new UsernameNotFoundException("User not found");
                });

        if (attendance.getCheckOut() != null) {
            log.warn("Duplicate check-out attempt for user: {}", email);
            throw new RuntimeException("Already check-out");
        }

        attendance.setCheckOut(LocalTime.now());
        long workedHour = Duration.between(
                attendance.getCheckIn(),
                attendance.getCheckOut()).toHours();

        if (workedHour < 2) {
            attendance.setStatus(AttendanceStatus.ABSENT);
            log.info("User {} worked {} hours → marked ABSENT", email, workedHour);
        }

        else if (workedHour <= 4 && workedHour > 2) {
            attendance.setStatus(AttendanceStatus.HALFDAY);
            log.info("User {} worked {} hours → marked HALFDAY", email, workedHour);
        }

        else {
            attendance.setStatus(AttendanceStatus.PRESENT);
            log.info("User {} worked {} hours → marked PRESENT", email, workedHour);
        }
        attendanceRepo.save(attendance);
        log.info("Check-out successful for user: {} at {}", email, attendance.getCheckOut());

        AttendanceLog logg = new AttendanceLog();
        logg.setEmail(email);
        logg.setAction("CHECK_OUT");
        logg.setTime(LocalDateTime.now());

        attendanceLogRepository.save(logg);
    }
}