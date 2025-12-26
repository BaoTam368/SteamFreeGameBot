package scheduler;

import java.time.*;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static long secondsUntilNext7AM() {

        ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

        ZonedDateTime now = ZonedDateTime.now(VN_ZONE);

        ZonedDateTime next7AM = now
                .withHour(7)
                .withMinute(0)
                .withSecond(0);

        if (now.compareTo(next7AM) >= 0) {
            next7AM = next7AM.plusDays(1);
        }

        return Duration.between(now, next7AM).getSeconds();
    }
}
