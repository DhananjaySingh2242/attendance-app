package ampliedtech.com.attendanceApp.mongoRepo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import ampliedtech.com.attendanceApp.document.AttendanceDocument;

@Repository
public interface AttendanceRepo extends MongoRepository<AttendanceDocument, String> {
    Optional<AttendanceDocument> findByKeycloakIdAndDate(String keycloakId, LocalDate date);
    Page<AttendanceDocument> findByEmailAndDate(String email, Pageable pageable);
    List<AttendanceDocument> findAllByEmailOrderByDateDesc(String email);
    List<AttendanceDocument> findByDate(LocalDate date);
}
