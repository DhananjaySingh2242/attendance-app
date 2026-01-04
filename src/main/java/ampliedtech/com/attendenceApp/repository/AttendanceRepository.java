package ampliedtech.com.attendenceApp.repository;
import ampliedtech.com.attendenceApp.entity.Attendance;
import ampliedtech.com.attendenceApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance,Long>{
Optional<Attendance> findByUserAndDate(User user, LocalDate date);
}
