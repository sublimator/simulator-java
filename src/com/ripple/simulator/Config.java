package com.ripple.simulator;

public class Config {
    public int num_nodes =            1000;
    public int num_malicious_nodes =    15;
    public int consensus_percent =      80;
    // Latencies in milliseconds
    // E2C - End to core, the latency from a node to a nearby node
    // C2C - Core to core, the additional latency when nodes are far
    public int min_e2c_latency =         5;
    public int max_e2c_latency =        50;
    public int min_c2c_latency =         5;
    public int max_c2c_latency =       200;
    public int num_outbound_links =     10;
    public int unl_min =                20;
    public int unl_max =                30;
    // unl datapoints we have to have before we change position
    public int unl_thresh =             (unl_min /2);
    // extra time we delay a message to coalesce/suppress
    public int base_delay =              1;
    // how many UNL votes you give yourself
    public int self_weight =             1;
    // how many packets can be "on the wire" per link per direction
    public int packets_on_wire =         3;
    public long random_seed = 0;
}
