package com.carterza.game;

import com.carterza.universe.Universe;
import squidpony.squidmath.Coord;

/**
 * Created by zachcarter on 12/9/16.
 */
public class Ship extends Actor {

    public Ship(long id, int speed, Universe universe) {
        super(id, speed, universe);
    }

    @Override
    public int act() {
        return 0;
    }
}
