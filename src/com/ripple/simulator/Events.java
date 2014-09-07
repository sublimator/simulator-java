package com.ripple.simulator;

import java.util.TreeMap;

/**
 * This is a lookup of network time to events
 */
public class Events extends TreeMap<Integer, Event> {
    public Event getDefault(int key) {
        Event event = super.get(key);
        if (event == null) {
            event = new Event();
            put(key, event);
        }
        return event;
    }
}
