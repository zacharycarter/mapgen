package com.carterza.planet.map.generator;

/**
 * Created by zachcarter on 12/15/16.
 */
public class MathHelper {

    public static int mod(int x, int m)
    {
        int r = x % m;
        return r < 0 ? r + m : r;
    }
}
