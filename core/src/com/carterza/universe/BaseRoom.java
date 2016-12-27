package com.carterza.universe;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.carterza.game.Actor;
import squidpony.squidgrid.gui.gdx.SquidLayers;
import squidpony.squidgrid.gui.gdx.SquidMessageBox;
import squidpony.squidmath.Coord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zachcarter on 12/9/16.
 */
public class BaseRoom implements Room {

    public static final int INTERNAL_ZOOM = 1;

    SpriteBatch spriteBatch;
    protected Universe universe;
    protected SquidLayers display;
    protected SquidMessageBox squidMessageBox;
    protected Stage stage;
    protected List<Actor> actors;
    protected String hash;

    public BaseRoom() {

    }

    public BaseRoom(Universe universe) {
        this.universe = universe;
        actors = new ArrayList<Actor>();
    }

    @Override
    public void render() {

    }

    @Override
    public void addActor(Actor actor) {

    }

    @Override
    public Rectangle getBounds() {
        return null;
    }

    @Override
    public Room getRoomAt(Coord position) {
        return null;
    }

    @Override
    public void enter() {

    }

    @Override
    public void exit() {

    }

    public void setHash(final String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    public Room neighbors(int offsetX, int offsetY) {
        return null;
    }

    public void dispose() {
        if(stage != null)
            stage.dispose();
    }
}
