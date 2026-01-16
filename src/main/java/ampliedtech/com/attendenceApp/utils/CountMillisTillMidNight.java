package ampliedtech.com.attendenceApp.utils;

import java.time.Duration;
import java.time.LocalDateTime;

public class CountMillisTillMidNight {
private CountMillisTillMidNight(){}
   public static long getMillisTillMidNight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
        return Duration.between(now, midnight).toMillis();
    }
}
