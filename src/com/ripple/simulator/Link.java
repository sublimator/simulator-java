package com.ripple.simulator;

/**
 * Represents a link between given nodes
 */
public class Link {
    // A connection from one node to another
    int to_node;
    int total_latency;

    // lm == last message ?
    // or link_message ?
    int lm_send_time;
    int lm_recv_time;
    Message lm;

    public Link(int to_node, int total_latency) {
        this.to_node = to_node;
        this.total_latency = total_latency;
        lm_send_time = 0;
        lm_recv_time = 0;
        lm = null;
    }
}
