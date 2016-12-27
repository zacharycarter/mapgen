package com.carterza.game;

import com.carterza.universe.Universe;
import squidpony.squidmath.Coord;

/**
 * Created by zachcarter on 12/9/16.
 */
public abstract class Actor {
    private final long id;
    final static double BASE_DELAY = 10;
    int speed;
    Universe universe;

    public Actor(long id, int speed, Universe universe)
    {
        this.id = id;
        this.speed = speed;
        this.universe = universe;
    }

    public long getId() {
        return id;
    }

    public boolean needsInput() {
        return false;
    }

    public double actionDelay() {
        return BASE_DELAY / this.speed;
    }

    public double actionDelay(double delay) {
        return delay / this.speed;
    }

    public abstract int act();

    public Universe getUniverse() {
        return universe;
    }
}