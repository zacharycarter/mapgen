package com.carterza.planet.map.generator;

import squidpony.squidmath.Coord;

/**
 * Created by zachcarter on 12/21/16.
 */
public class CoordinateElevation implements Comparable<CoordinateElevation> {
    Coord coord;
    double elevation;

    public CoordinateElevation(Coord coord, double elevation) {
        this.coord = coord;
        this.elevation = elevation;
    }

    @Override
    public int compareTo(CoordinateElevation o) {
        return Double.compare(elevation, o.elevation);
    }
}
