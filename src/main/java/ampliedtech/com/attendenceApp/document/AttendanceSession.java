package ampliedtech.com.attendenceApp.document;

import java.time.LocalDateTime;

public class AttendanceSession {
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Long durationMinutes;

    public AttendanceSession() {}
    public AttendanceSession(LocalDateTime checkIn, LocalDateTime checkOut, Long durationMinutes) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.durationMinutes = durationMinutes;
    }
    public LocalDateTime getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDateTime checkIn) { this.checkIn = checkIn; }
    public LocalDateTime getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDateTime checkOut) { this.checkOut = checkOut; }
    public Long getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(long durationMinutes) { this.durationMinutes = durationMinutes; }

    public static AttendanceSessionBuilder builder() { return new AttendanceSessionBuilder(); }
    public static final class AttendanceSessionBuilder {
        private LocalDateTime checkIn;
        private LocalDateTime checkOut;
        private Long durationMinutes;
        public AttendanceSessionBuilder checkIn(LocalDateTime checkIn) { this.checkIn = checkIn; return this; }
        public AttendanceSessionBuilder checkOut(LocalDateTime checkOut) { this.checkOut = checkOut; return this; }
        public AttendanceSessionBuilder durationMinutes(Long durationMinutes) { this.durationMinutes = durationMinutes; return this; }
        public AttendanceSession build() {
            AttendanceSession s = new AttendanceSession();
            s.setCheckIn(checkIn);
            s.setCheckOut(checkOut);
            s.setDurationMinutes(durationMinutes != null ? durationMinutes : 0L);
            return s;
        }
    }
}
