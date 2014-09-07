A WIP port of the ripple consensus simulator to Java

```java
package com.ripple.simulator;

import java.text.MessageFormat;

public class Simulator {

    public static void main(String[] args) {
        new Simulator().simulate();
    }

    public void simulate() {
        Config conf = new Config();
        // could very well get this from a conf file
        conf.consensus_percent = 80;
        conf.random_seed = 80;

        Network network = new Network(conf);

        log("Creating nodes");
        network.createNodes();

        log("Creating links");
        network.setUpLinks();

        log("Creating initial messages");
        network.setUpMessages();

        log("Created {0} events", network.eventsSize());
        network.awaitConsensus();

        int mc = network.messageCount();
        log("Consensus reached in {0} ms with {1} messages on the wire", network.master_time, mc);

        long total_messages_sent = network.totalMessagesSent();
        log("The average node sent {0} messages", total_messages_sent / network.total_nodes);
    }

    public static void log(String s, Object... args) {
        System.out.println(MessageFormat.format(s, args));
    }
}
```

```
Creating nodes
Creating links
Creating initial messages
Created 272 events
Time: 100 ms 500/500
Time: 200 ms 495/505
Time: 300 ms 394/606
Time: 400 ms 368/632
Time: 500 ms 215/785
Consensus reached in 504 ms with 73,933 messages on the wire
The average node sent 190 messages

Process finished with exit code 0
```