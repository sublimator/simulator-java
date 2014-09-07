package com.ripple.simulator;

import java.util.ArrayList;

/**
 * Each `Event` represents a point in time, with an array of messages
 * that will be received.
 */
public class Event {
    ArrayList<Message> messages = new ArrayList<Message>();
    Message addMessage(Message message) {
        messages.add(message);
        return message;
    }
}
