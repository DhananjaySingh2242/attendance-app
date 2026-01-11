package ampliedtech.com.attendenceApp.document;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import ampliedtech.com.attendenceApp.entity.AttendanceStatus;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "attendance")
@CompoundIndex(
    name = "unique_email_date_idx",
    def = "{'email': 1, 'date': 1}",
    unique = true
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceDocument {
@Id
private String id;
private Long userId;
private String email;
private LocalDate date;

private List<AttendanceSession> sessions;
private long totalDurationMinutes;

private AttendanceStatus status;


}
