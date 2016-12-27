package com.carterza.universe;

import com.badlogic.gdx.math.Rectangle;
import com.carterza.game.Actor;
import com.carterza.game.Player;
import squidpony.squidmath.Coord;

/**
 * Created by zachcarter on 12/9/16.
 */
public interface Room {
    void render();

    void addActor(Actor actor);

    Rectangle getBounds();

    Room getRoomAt(Coord position);

    void enter();

    void exit();
}
