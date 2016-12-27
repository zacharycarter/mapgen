package com.carterza.planet.map;

/**
 * Created by zachcarter on 12/15/16.
 */
public enum HeightType {
    DeepWater(1),
    ShallowWater(2),
    MediumWater(3),
    CoastalWater(4),
    Shore(5),
    Sand(6),
    Grass(7),
    Forest(8),
    Rock(9),
    Snow(10),
    River(11),
    DebugSource(12),
    DebugDestination(13),
    DebugCoastline(14);

    private int numVal;

    HeightType(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
