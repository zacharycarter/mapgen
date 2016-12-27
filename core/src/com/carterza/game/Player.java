package com.carterza.game;

import com.carterza.game.action.Action;
import com.carterza.universe.Universe;
import squidpony.squidmath.Coord;

/**
 * Created by zachcarter on 12/9/16.
 */
public class Player extends Character {

    Action nextAction;

    public Player(long id, int speed, Universe universe, Coord position) {
        super(id, speed, universe, position);
    }

    @Override
    public boolean needsInput() {
        return nextAction == null;
    }

    public void setNextAction(Action action) {
        nextAction = action;
    }

    @Override
    public int act() {
        int actionCost = nextAction.getCost();
        nextAction = null;
        return actionCost;
    }
}

