package ampliedtech.com.attendenceApp.document;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.mongodb.core.mapping.Document;

import ampliedtech.com.attendenceApp.entity.AttendanceStatus;
import jakarta.persistence.Id;
import lombok.Data;

@Document(collection = "attendance")
@Data
public class AttendanceDocument {
@Id
private String id;
private Long userId;
private String email;
private LocalDate date;

private LocalTime checkIn;
private LocalTime checkOut;

private AttendanceStatus status;


}
