package com.carterza.planet.map;

/**
 * Created by zachcarter on 12/15/16.
 */
public class MapData {
    public double[][] data;
    public double min;
    public double max;

    public MapData(int width, int height) {
        data = new double[width][height];
        min = Float.MAX_VALUE;
        max = Float.MIN_VALUE;
    }

}
