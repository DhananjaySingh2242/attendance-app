package ampliedtech.com.attendenceApp.document;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import ampliedtech.com.attendenceApp.entity.AttendanceStatus;
import jakarta.persistence.Id;

@Document(collection = "attendance")
@CompoundIndex(
    name = "unique_email_date_idx",
    def = "{'email': 1, 'date': 1}",
    unique = true
)
public class AttendanceDocument {
    @Id
    private String id;
    private String keycloakId;
    private String email;
    private LocalDate date;
    private List<AttendanceSession> sessions;
    private long totalDurationMinutes;
    private AttendanceStatus status;

    public String getKeycloakId() { return keycloakId; }
    public void setKeycloakId(String keycloakId) { this.keycloakId = keycloakId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<AttendanceSession> getSessions() { return sessions; }
    public void setSessions(List<AttendanceSession> sessions) { this.sessions = sessions; }
    public long getTotalDurationMinutes() { return totalDurationMinutes; }
    public void setTotalDurationMinutes(long totalDurationMinutes) { this.totalDurationMinutes = totalDurationMinutes; }

    public AttendanceDocument() {}
    public AttendanceDocument(String id, String keycloakId, String email, LocalDate date, List<AttendanceSession> sessions, long totalDurationMinutes, AttendanceStatus status) {
        this.id = id;
        this.keycloakId = keycloakId;
        this.email = email;
        this.date = date;
        this.sessions = sessions;
        this.totalDurationMinutes = totalDurationMinutes;
        this.status = status;
    }

    public static AttendanceDocumentBuilder builder() { return new AttendanceDocumentBuilder(); }
    public static final class AttendanceDocumentBuilder {
        private String id;
        private String keycloakId;
        private String email;
        private LocalDate date;
        private List<AttendanceSession> sessions;
        private long totalDurationMinutes;
        private AttendanceStatus status;
        public AttendanceDocumentBuilder id(String id) { this.id = id; return this; }
        public AttendanceDocumentBuilder keycloakId(String keycloakId) { this.keycloakId = keycloakId; return this; }
        public AttendanceDocumentBuilder email(String email) { this.email = email; return this; }
        public AttendanceDocumentBuilder date(LocalDate date) { this.date = date; return this; }
        public AttendanceDocumentBuilder sessions(List<AttendanceSession> sessions) { this.sessions = sessions; return this; }
        public AttendanceDocumentBuilder totalDurationMinutes(long totalDurationMinutes) { this.totalDurationMinutes = totalDurationMinutes; return this; }
        public AttendanceDocumentBuilder status(AttendanceStatus status) { this.status = status; return this; }
        public AttendanceDocument build() {
            AttendanceDocument d = new AttendanceDocument();
            d.setId(id);
            d.setKeycloakId(keycloakId);
            d.setEmail(email);
            d.setDate(date);
            d.setSessions(sessions);
            d.setTotalDurationMinutes(totalDurationMinutes);
            d.setStatus(status);
            return d;
        }
    }
}