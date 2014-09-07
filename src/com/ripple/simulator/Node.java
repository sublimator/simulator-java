package com.ripple.simulator;

import java.util.ArrayList;
import java.util.Map;

public class Node {
    public int nth, e2c_latency;
    public Config config;

    public ArrayList<Integer> unl;
    public ArrayList<Link> links;

    // node states
    public int[] knowledge;
    // node time stamps
    public int[] nts;

    public int messages_sent, messages_received;

    public Node(int nth, int total_nodes, Config config) {
        this.nth = nth;
        this.config = config;

        links = new ArrayList<Link>();
        unl = new ArrayList<Integer>();

        messages_received = 0;
        messages_sent = 0;

        knowledge = new int[total_nodes];
        nts = new int[total_nodes];
    }

    boolean isOnUNL(int j) {
        for (Integer aInteger : unl) {
            if (aInteger == j) {
                return true;
            }
        }
        return false;
    }
    boolean hasLinkTo(int j) {
        for (Link link : links) {
            if (link.to_node == j) {
                return true;
            }
        }
        return false;
    }

    public void receiveMessage(Message m, Network network) {
        ++messages_received;
        int network_time = network.master_time;

        for (Link link : links) {
            if (link.to_node == m.from_node &&
                link.lm_send_time >= network_time) {
                link.lm.subPositions(m.data);
                break;
            }
        }

        NodeStates changes = getMessageChanges(m);

        if (changes.isEmpty()) {
            return;
        }

        int unl_count = 0, unl_balance = 0;
        for (int node_id : unl) {
            if (knowledge[node_id] == 1) {
                ++unl_count;
                ++unl_balance;
            }
            if (knowledge[node_id] == -1) {
                ++unl_count;
                --unl_balance;
            }
        }

        if (nth < config.num_malicious_nodes) {
            unl_balance = -unl_balance;
        }

        unl_balance -= network_time / 250;

        boolean pos_change=false;
        if (unl_count >= config.unl_thresh) {
            if (knowledge[nth] == 1 && (unl_balance < (-config.self_weight))) {
                knowledge[nth] = -1;
                --network.nodes_positive;
                ++network.nodes_negative;
                changes.put(nth, new NodeState(nth, ++nts[nth], -1));
                pos_change=true;
            } else if (knowledge[nth] == -1 && (unl_balance > config.self_weight)) {
                knowledge[nth] = 1;
                ++network.nodes_positive;
                --network.nodes_negative;
                changes.put(nth, new NodeState(nth, ++nts[nth], +1));
                pos_change = true;
            }
        }

        for (Link link : links) {
            if (pos_change || (link.to_node != m.from_node)) {
                if (link.lm_send_time > network_time) {
                    link.lm.addPositions(changes);
                } else {
                    int send_time = network_time;
                    if (!pos_change) {
                        send_time += config.base_delay;
                        if (link.lm_recv_time > send_time) {
                            send_time += link.total_latency / config.packets_on_wire;
                        }
                    }
                    network.sendMessage(new Message(nth, link.to_node, changes), link, send_time);
                    messages_sent++;
                }
            }
        }
    }

    private NodeStates getMessageChanges(Message m) {
        NodeStates changes = new NodeStates();

        for (Map.Entry<Integer, NodeState> change : m.data.entrySet()) {
            int node_id = change.getKey();
            NodeState new_state = change.getValue();

            if (node_id != nth &&
                knowledge[node_id] != new_state.state &&
                new_state.ts > nts[node_id])
            {
                knowledge[node_id] = new_state.state;
                nts[node_id] = new_state.ts;
                changes.put(node_id, new_state);
            }
        }
        return changes;
    }

}
