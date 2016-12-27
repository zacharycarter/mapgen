package com.carterza.game.action;

import com.carterza.game.Actor;
import com.carterza.game.Player;

abstract public class Action {
    private Actor actor;
    int cost;

    public Action(Actor actor, int cost) {
        this.actor = actor;
        this.cost = cost;
    }

    public Actor getActor() {
        return actor;
    }

    public abstract ActionResult perform();

    public int getCost() {
        return this.cost;
    }

    public void setActor(Player actor) {
        this.actor = actor;
    }
}
