package com.carterza.planet.map;

/**
 * Created by zachcarter on 12/15/16.
 */
public enum  MoistureType {
    Wettest(5),
    Wetter(4),
    Wet(3),
    Dry(2),
    Dryer(1),
    Dryest(0);

    private int numVal;

    MoistureType(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
