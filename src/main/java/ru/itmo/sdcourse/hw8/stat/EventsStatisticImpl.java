package ru.itmo.sdcourse.hw8.stat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class EventsStatisticImpl implements EventsStatistic {
    private static final Duration DEFAULT_DURATION_LIMIT = Duration.ofHours(1);

    private final Map<String, Queue<Instant>> eventLog = new HashMap<>();
    private final Clock clock;
    private final Duration durationLimit;

    public EventsStatisticImpl() {
        this(Clock.systemDefaultZone(), DEFAULT_DURATION_LIMIT);
    }

    public EventsStatisticImpl(Duration durationLimit) {
        this(Clock.systemDefaultZone(), durationLimit);
    }

    public EventsStatisticImpl(Clock clock) {
        this(clock, DEFAULT_DURATION_LIMIT);
    }

    public EventsStatisticImpl(Clock clock, Duration durationLimit) {
        this.clock = clock;
        this.durationLimit = durationLimit;
    }

    @Override
    public void incEvent(String name) {
        getReducedBucket(name, false).orElseThrow().add(clock.instant());
    }

    @Override
    public EventStatistic getEventStatisticByName(String name) {
        return newEventStatistic(getReducedBucket(name, true).map(Queue::size).orElse(0));
    }

    @Override
    public EventStatistic getAllEventStatistic() {
        return newEventStatistic(eventLog.keySet().stream()
                .map(name -> getReducedBucket(name, true))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Queue::size)
                .mapToLong(Number::longValue)
                .sum());
    }

    @Override
    public Map<String, EventStatistic> getAllEventStatistics() {
        Map<String, EventStatistic> result = new HashMap<>();
        new HashSet<>(eventLog.keySet()).forEach(name ->
                getReducedBucket(name, true)
                        .map(Queue::size)
                        .map(this::newEventStatistic)
                        .ifPresent(eventStatistic -> result.put(name, eventStatistic))
        );
        return result;
    }

    @Override
    public String printStatistic() {
        StringBuilder result = new StringBuilder();
        Map<String, EventStatistic> allEventStatistics = getAllEventStatistics();

        OptionalInt maxEventNameLength = allEventStatistics.keySet().stream().mapToInt(String::length).max();
        if (maxEventNameLength.isEmpty()) {
            result.append("No events registered in last ").append(durationLimit.toSeconds()).append(" seconds");
            return result.toString();
        }

        final String NAME_HEADER = "Event";
        final String COUNT_HEADER = "Count      ";
        final String DURATION_HEADER = "Duration";

        result.append(NAME_HEADER)
                .append(" ".repeat(Math.max(0, maxEventNameLength.getAsInt() - NAME_HEADER.length())))
                .append(" | ")
                .append(COUNT_HEADER)
                .append(" | ")
                .append(DURATION_HEADER)
                .append('\n');

        result.append("-".repeat(result.length())).append('\n');

        allEventStatistics.forEach((name, statistic) -> {
            var eventsCountStr = String.valueOf(statistic.eventsCount());
            result.append(name)
                    .append(" ".repeat(Math.max(0, maxEventNameLength.getAsInt() - name.length())))
                    .append(" | ")
                    .append(eventsCountStr)
                    .append(" ".repeat(Math.max(0, COUNT_HEADER.length() - eventsCountStr.length())))
                    .append(" | ")
                    .append(statistic.duration())
                    .append("\n");
        });

        return result.toString();
    }

    private EventStatistic newEventStatistic(long eventsCount) {
        return new EventStatistic(eventsCount, durationLimit);
    }

    private boolean isExpired(Instant eventInstant) {
        return clock.instant().isAfter(eventInstant.plus(durationLimit));
    }

    private Optional<Queue<Instant>> getReducedBucket(String name, boolean removeIfEmpty) {
        Queue<Instant> bucket = eventLog.computeIfAbsent(name, _k -> new LinkedList<>());
        while (!bucket.isEmpty() && isExpired(bucket.element()))
            bucket.remove();

        if (bucket.isEmpty() && removeIfEmpty) {
            eventLog.remove(name);
            return Optional.empty();
        } else
            return Optional.of(bucket);
    }
}
