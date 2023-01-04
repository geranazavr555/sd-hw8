package ru.itmo.sdcourse.hw8.stat;

import org.junit.Test;
import ru.itmo.sdcourse.hw8.clock.MutableFixedClock;

import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EventsStatisticImplTest {
    @Test
    public void empty() {
        var statistics = new EventsStatisticImpl();
        assertTrue(statistics.getAllEventStatistics().isEmpty());

        assertEquals(0, statistics.getEventStatisticByName("abacaba").eventsCount());
        assertEquals(0.0, statistics.getEventStatisticByName("abacaba").rpm(), 1e-10);

        assertTrue(statistics.printStatistic().contains("No events"));
    }

    @Test
    public void simple() {
        var clock = MutableFixedClock.now();
        var statistics = new EventsStatisticImpl(clock);

        statistics.incEvent("abacaba");
        statistics.incEvent("abacaba");
        statistics.incEvent("kek");
        statistics.incEvent("abacaba");

        var allEvents = statistics.getAllEventStatistics();
        assertTrue(allEvents.containsKey("abacaba"));
        assertTrue(allEvents.containsKey("kek"));
        assertEquals(2, allEvents.size());

        assertEquals(3, allEvents.get("abacaba").eventsCount());
        assertEquals(1, allEvents.get("kek").eventsCount());
        assertEquals(3.0 / 60, allEvents.get("abacaba").rpm(), 1e-10);
        assertEquals(1.0 / 60, allEvents.get("kek").rpm(), 1e-10);
        assertEquals(Duration.ofHours(1), allEvents.get("abacaba").duration());
        assertEquals(Duration.ofHours(1), allEvents.get("kek").duration());

        var eventStat = statistics.getAllEventStatistic();
        assertEquals(4, eventStat.eventsCount());
        assertEquals(4.0 / 60, eventStat.rpm(), 1e-10);
        assertEquals(Duration.ofHours(1), eventStat.duration());

        eventStat = statistics.getEventStatisticByName("abacaba");

        assertEquals(3, eventStat.eventsCount());
        assertEquals(3.0 / 60, eventStat.rpm(), 1e-10);
        assertEquals(Duration.ofHours(1), eventStat.duration());

        eventStat = statistics.getEventStatisticByName("kek");

        assertEquals(1, eventStat.eventsCount());
        assertEquals(1.0 / 60, eventStat.rpm(), 1e-10);
        assertEquals(Duration.ofHours(1), eventStat.duration());
    }

    @Test
    public void delays() {
        var clock = MutableFixedClock.now();
        var statistics = new EventsStatisticImpl(clock);

        statistics.incEvent("abacaba");
        statistics.incEvent("abacaba");
        statistics.incEvent("kek");
        statistics.incEvent("abacaba");
        statistics.incEvent("LOL");

        clock.add(Duration.ofHours(1));

        statistics.incEvent("abacaba");
        statistics.incEvent("kek");

        clock.add(Duration.ofSeconds(3));

        statistics.incEvent("kek");
        statistics.incEvent("abacaba");
        statistics.incEvent("abacaba");

        var allEvents = statistics.getAllEventStatistics();
        assertTrue(allEvents.containsKey("abacaba"));
        assertTrue(allEvents.containsKey("kek"));
        assertEquals(2, allEvents.size());

        var eventStat = statistics.getEventStatisticByName("abacaba");

        assertEquals(3, eventStat.eventsCount());
        assertEquals(3.0 / 60, eventStat.rpm(), 1e-10);
        assertEquals(Duration.ofHours(1), eventStat.duration());

        eventStat = statistics.getEventStatisticByName("kek");

        assertEquals(2, eventStat.eventsCount());
        assertEquals(2.0 / 60, eventStat.rpm(), 1e-10);
        assertEquals(Duration.ofHours(1), eventStat.duration());
    }

    @Test
    public void print() {
        var statistics = new EventsStatisticImpl();
        statistics.incEvent("abacaba");
        statistics.incEvent("abacaba");
        statistics.incEvent("kek");
        statistics.incEvent("abacaba");

        var s = statistics.printStatistic();
        System.out.println(s);
        String[] lines = s.split("\n");
        assertEquals(4, lines.length);
    }
}
