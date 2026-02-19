package ampliedtech.com.attendanceApp.mongoRepo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import ampliedtech.com.attendanceApp.document.AttendanceLog;

@Repository
public interface AttendanceLogRepository extends MongoRepository<AttendanceLog, String> {
}