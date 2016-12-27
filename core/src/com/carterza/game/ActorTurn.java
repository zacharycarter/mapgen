package com.carterza.game;

/**
 * Created by zachcarter on 12/9/16.
 */
public class ActorTurn implements Comparable<ActorTurn> {
    private double time;
    long timestamp;
    Actor actor;

    public ActorTurn(Actor actor, double time, long timestamp) {
        this.actor = actor;
        this.time = time;
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(ActorTurn o) {
        if(this.time != o.time) {
            return Double.compare(this.time, o.time);
        }
        return Long.compare(this.timestamp, o.timestamp);
    }

    public double getTime() {
        return this.time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public Actor getActor() {
        return this.actor;
    }
}
