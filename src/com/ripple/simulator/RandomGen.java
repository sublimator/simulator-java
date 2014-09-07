package com.ripple.simulator;

import java.util.Random;

public class RandomGen {
    private Config config;
    private Random rand;

    public RandomGen(Config config) {
        this.config = config;
        rand = new Random(config.random_seed);
    }

    public int e2c(){ return randomBetweenInclusive(config.min_e2c_latency, config.max_e2c_latency); }
    public int c2c(){ return randomBetweenInclusive(config.min_c2c_latency, config.max_c2c_latency); }
    public int unl(){ return randomBetweenInclusive(config.unl_min, config.unl_max); }
    public int node(){ return randomBetweenInclusive(0, config.num_nodes - 1); }

    private int randomBetweenInclusive(int a, int b) {
        return a + rand.nextInt(b + 1 - a);
    }
}
