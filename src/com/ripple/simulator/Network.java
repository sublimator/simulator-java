package com.ripple.simulator;

import java.util.Map;

public class Network {
    public int nodes_positive=0;
    public int nodes_negative=0;

    public int master_time;
    Events events;
    Node[] nodes;
    int total_nodes;
    RandomGen rand;
    Config config;

    public Network(Config conf) {
        master_time = 0;
        events = new Events();
        config = conf;
        nodes = new Node[conf.num_nodes];
        total_nodes = nodes.length;
        rand = new RandomGen(conf);
    }

    public Node[] getNodes() {
        return nodes;
    }

    public void sendMessage(Message message, Link link, int send_time) {
        if (message.to_node != link.to_node) throw new AssertionError();
        link.lm_send_time = send_time;
        link.lm_recv_time = send_time + link.total_latency;
        link.lm = events.getDefault(link.lm_recv_time).addMessage(message);
    }

    public int messageCount() {
        int mc = 0;
        for (Event event : events.values()) {
            mc += event.messages.size();
        }
        return mc;
    }

    public boolean reachedConsensus() {
        return (nodes_positive > config.num_nodes * config.consensus_percent / 100) ||
                (nodes_negative> config.num_nodes * config.consensus_percent / 100);
    }

    void createNodes() {
        for (int i = 0; i < config.num_nodes; i++) {
            Node node = new Node(i, config.num_nodes, config);
            nodes[i] = node;
            node.e2c_latency = rand.e2c();

            // 50/50
            if (i % 2 == 1) {
                node.knowledge[i] = 1;
                node.nts[i] = 1;
                ++nodes_positive;
            } else {
                node.knowledge[i] = -1;
                node.nts[i] = 1;
                ++nodes_negative;
            }

            // Build our unl
            int unl_count = rand.unl();
            while (unl_count > 0) {
                int cn = rand.node();
                if ((cn != i) && !node.isOnUNL(cn)) {
                    node.unl.add(cn);
                    --unl_count;
                }
            }
        }
    }

    void setUpLinks() {
        for (int self = 0; self < config.num_nodes; self++) {
            int links = config.num_outbound_links;
            while (links > 0) {
                int to = rand.node();
                if ((to != self) && !nodes[self].hasLinkTo(to)) {
                    // link_latency == link latency ?
                    int link_latency = nodes[self].e2c_latency +
                            nodes[to].e2c_latency +
                            rand.c2c();

                    nodes[self].links.add(new Link(to, link_latency));
                    nodes[to].links.add(new Link(self, link_latency));
                    --links;
                }
            }
        }
    }

    void setUpMessages() {
        for (int i = 0; i < total_nodes; i++) {
            for (Link link : nodes[i].links) {
                Message m = new Message(i, link.to_node);
                m.data.put(i, new NodeState(i, 1, nodes[i].knowledge[i]));
                sendMessage(m, link, 0);
            }
        }
    }

    int eventsSize() {
        return events.size();
    }

    Map.Entry<Integer, Event> nextTimedEvent() {
        return events.firstEntry();
    }

    void awaitConsensus() {
        do {
            // If we've reach consensus, stop
            if (reachedConsensus()) {
                break;
            }

            // The first entry will be the next time / Event where messages are received
            Map.Entry<Integer, Event> timedEvent = nextTimedEvent();
            // This means something wrong, we should ricochet until consensus is reached
            if (timedEvent == null) {
                Simulator.log("Fatal: radio silence");
                System.exit(0);
            }

            int time = timedEvent.getKey();
            Event event = timedEvent.getValue();
            events.remove(time);

            if ((time / 100) > (master_time / 100)) {
                Simulator.log("Time: {0} ms {1}/{2}", time, nodes_positive, nodes_negative);
            }

            master_time = time;

            // Route the messages
            for (Message message : event.messages) {
                if (!message.data.isEmpty()) {
                    nodes[message.to_node].receiveMessage(message, this);
                } else {
                    --nodes[message.from_node].messages_sent;
                }
            }


        } while (true);
    }

    long totalMessagesSent() {
        long total_messages_sent = 0;
        for (int i = 0; i < total_nodes; i++) {
            total_messages_sent += getNodes()[i].messages_sent;
        }
        return total_messages_sent;
    }
}
