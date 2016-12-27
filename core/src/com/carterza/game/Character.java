package com.carterza.game;

import com.carterza.universe.Universe;
import squidpony.squidmath.Coord;

/**
 * Created by zachcarter on 12/9/16.
 */
public abstract class Character extends Actor {

    protected Coord position;
    private char symbol;

    public Character(long id, int speed, Universe universe, Coord position) {
        super(id, speed, universe);
        this.position = position;
    }

    public Coord getPosition() {
        return position;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setPosition(Coord newPosition) {
        position = newPosition;
    }
}
