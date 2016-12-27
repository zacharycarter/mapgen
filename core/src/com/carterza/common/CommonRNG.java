package com.carterza.common;


import squidpony.squidmath.LightRNG;
import squidpony.squidmath.RNG;

/**
 * Created by zachcarter on 12/9/16.
 */
public class CommonRNG {
    private static final LightRNG lightRNG = new LightRNG();
    private static RNG rng = new RNG(lightRNG);

    public static RNG getRng() {
        return rng;
    }

    public static void setSeed(final String seed) {
        rng = new RNG(seed);
    }
}
