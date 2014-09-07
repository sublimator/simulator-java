package com.ripple.simulator;

// simulates non-infinite bandwidth
public class NodeState {
    // A NodeState as propagated by the network
    int node;
    int ts;
    int state;

    public NodeState(int node, int ts, int state) {
        this.node = node; // the `nth` node
        this.ts = ts; // ms
        this.state = state; // either 1, or - 1
    }
}
