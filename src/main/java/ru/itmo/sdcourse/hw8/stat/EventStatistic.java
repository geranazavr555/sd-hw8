package ru.itmo.sdcourse.hw8.stat;

import java.time.Duration;

public record EventStatistic(long eventsCount, Duration duration) {
    public double rpm() {
        double minutes = duration.toMinutes();
        return eventsCount / (minutes > 0 ? minutes : 1.0);
    }
}
