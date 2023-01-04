package ru.itmo.sdcourse.hw8.clock;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

public class MutableFixedClock extends Clock {
    private final ZoneId zoneId;
    private Instant instant;

    public MutableFixedClock(Instant instant) {
        this(instant, ZoneId.systemDefault());
    }

    public MutableFixedClock(Instant instant, ZoneId zoneId) {
        this.instant = instant;
        this.zoneId = zoneId;
    }

    @Override
    public ZoneId getZone() {
        return zoneId;
    }

    @Override
    public MutableFixedClock withZone(ZoneId zone) {
        return new MutableFixedClock(instant, zone);
    }

    @Override
    public Instant instant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public void add(Duration duration) {
        this.setInstant(instant.plus(duration));
    }

    public static MutableFixedClock now() {
        return new MutableFixedClock(Instant.now());
    }
}
