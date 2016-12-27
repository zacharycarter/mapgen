package com.carterza.planet.map;

/**
 * Created by zachcarter on 12/15/16.
 */
public enum HeatType {
    Coldest(0),
    Colder(1),
    Cold(2),
    Warm(3),
    Warmer(4),
    Warmest(5);

    private int numVal;

    HeatType(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
