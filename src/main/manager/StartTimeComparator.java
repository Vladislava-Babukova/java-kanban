package main.manager;

import java.time.LocalDateTime;
import java.util.Comparator;

class StartTimeComparator implements Comparator<LocalDateTime> {
    @Override
    public int compare(LocalDateTime time1, LocalDateTime time2) {
        return time1.compareTo(time2);
    }
}
