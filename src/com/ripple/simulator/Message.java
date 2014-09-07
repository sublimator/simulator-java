package com.ripple.simulator;

public class Message {
    // A message sent from one node to another,
    // containing the positions taken

    int from_node, to_node;
    NodeStates data;

    public Message(int from_node, int to_node, NodeStates data) {
        this.from_node = from_node;
        this.to_node = to_node;
        this.data = data;
    }

    public Message(int from_node, int to_node) {
        this.from_node = from_node;
        this.to_node = to_node;
        data = new NodeStates();
    }

    public void addPositions(NodeStates updates) {
        for (int node_id  : updates.keySet()) {
            // don't tell a node about itself

            if (node_id != to_node) {
                NodeState stored = data.get(node_id);
                NodeState updated = updates.get(node_id);

                if (stored == null || node_id == 0) {
                    data.put(node_id, updated);
                }
                else {
                    if (updated.ts > stored.ts) {
                        stored.ts = updated.ts;
                        stored.state = updated.state;
                    }
                }
            }
        }
    }

    public void subPositions(NodeStates received) {
        for (int node_id  : received.keySet()) {
            NodeState state = received.get(node_id);

            if (node_id != to_node) {
                NodeState stored = data.get(node_id);
                if (stored != null && stored.ts <= state.ts) {
                    data.remove(node_id);
                }
            }
        }
    }
}
