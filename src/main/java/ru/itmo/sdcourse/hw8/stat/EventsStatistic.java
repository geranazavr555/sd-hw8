package ru.itmo.sdcourse.hw8.stat;

import java.util.Map;

public interface EventsStatistic {
    void incEvent(String name);

    EventStatistic getEventStatisticByName(String name);

    EventStatistic getAllEventStatistic();

    Map<String, EventStatistic> getAllEventStatistics();

    String printStatistic();
}
