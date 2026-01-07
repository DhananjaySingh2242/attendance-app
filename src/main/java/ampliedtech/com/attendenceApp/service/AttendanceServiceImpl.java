package ampliedtech.com.attendenceApp.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ampliedtech.com.attendenceApp.entity.Attendance;
import ampliedtech.com.attendenceApp.entity.AttendanceStatus;
import ampliedtech.com.attendenceApp.entity.User;
import ampliedtech.com.attendenceApp.repository.AttendanceRepository;
import ampliedtech.com.attendenceApp.repository.UserRepository;

@Service
public class AttendanceServiceImpl implements AttendenceService {
    private static final Logger log = LoggerFactory.getLogger(AttendanceServiceImpl.class);
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository, UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void checkIn(String email) {
        log.info("Check-in attempt for user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Check-in failed. User not found: {}", email);
                    return new UsernameNotFoundException("User Not Found");
                });
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setDate(LocalDate.now());
        attendance.setCheckIn(LocalTime.now());
        attendance.setStatus(AttendanceStatus.PRESENT);
        attendanceRepository.save(attendance);
        log.info("Check-in successful for user: {} at {}", email, attendance.getCheckIn());
    }

    @Transactional
    @Override
    public void checkOut(String email) {
        log.info("Check-out attempt for user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Check-out failed. User not found: {}", email);
                    return new UsernameNotFoundException("User not found");
                });

        Attendance attendance = attendanceRepository
                .findByUserAndDate(user, LocalDate.now())
                .orElseThrow(() -> {
                    log.warn("Check-out failed. No check-in found for user: {}", email);
                    return new RuntimeException("Check-in not found for today");
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

        attendanceRepository.save(attendance);
        log.info("Check-out successful for user: {} at {}", email, attendance.getCheckOut());
    }
}