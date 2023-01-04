package ru.itmo.sdcourse.hw8;

import ru.itmo.sdcourse.hw8.stat.EventsStatistic;
import ru.itmo.sdcourse.hw8.stat.EventsStatisticImpl;

public class Main {
    public static void main(String[] args) {
        EventsStatistic eventsStatistic = new EventsStatisticImpl();
        eventsStatistic.incEvent("test1");
        eventsStatistic.incEvent("test1");
        eventsStatistic.incEvent("abacaba event");
        eventsStatistic.incEvent("abacaba event");
        eventsStatistic.incEvent("abacaba event");
        eventsStatistic.incEvent("test1");
        eventsStatistic.incEvent("abacaba event");
        System.out.println(eventsStatistic.printStatistic());
    }
}
