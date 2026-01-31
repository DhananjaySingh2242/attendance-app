package ampliedtech.com.attendenceApp.mongoRepo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import ampliedtech.com.attendenceApp.document.AttendanceLog;

@Repository
public interface AttendanceLogRepository extends MongoRepository<AttendanceLog, String> {
}