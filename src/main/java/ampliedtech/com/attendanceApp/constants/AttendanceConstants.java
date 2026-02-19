package ampliedtech.com.attendanceApp.constants;

/**
 * Business rules for attendance status. Minutes are total duration per day.
 */
public final class AttendanceConstants {

    private AttendanceConstants() {}

    /** Minutes below this = ABSENT */
    public static final long MINUTES_ABSENT_THRESHOLD = 120;
    /** Minutes below this (and above absent) = HALFDAY */
    public static final long MINUTES_HALF_DAY_THRESHOLD = 240;
    /** Default Redis TTL when midnight calculation fails (1 hour in ms) */
    public static final long DEFAULT_REDIS_TTL_MS = 60 * 60 * 1000L;
    /** Redis key prefix for active check-in */
    public static final String REDIS_KEY_ACTIVE_PREFIX = "attendance:active:";
}
