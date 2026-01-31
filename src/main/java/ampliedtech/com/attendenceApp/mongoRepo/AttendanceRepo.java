package ampliedtech.com.attendenceApp.mongoRepo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import ampliedtech.com.attendenceApp.document.AttendanceDocument;
import ampliedtech.com.attendenceApp.responseDto.AttendanceResponse;

@Repository
public interface AttendanceRepo extends MongoRepository<AttendanceDocument, String> {
    Optional<AttendanceDocument> findByEmailAndDate(String email, LocalDate date);
    Page<AttendanceDocument> findByEmailAndDate(String email,Pageable pageable);
    List<AttendanceDocument>findAllByEmailOrderByDateDesc(String email);
    List<AttendanceResponse>findByDate(LocalDate date);
}
