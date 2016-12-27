package com.carterza.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.carterza.Derelict;
import com.carterza.input.InputHandler;
import com.carterza.universe.Galaxy;
import com.carterza.universe.Universe;
import squidpony.squidgrid.gui.gdx.SquidInput;

/**
 * Created by zachcarter on 12/9/16.
 */
public class GameScreen extends AbstractScreen {

    Universe universe;
    Galaxy galaxy;
    SpriteBatch spriteBatch;
    SquidInput squidInput;

    public GameScreen(Derelict derelict) {
        super(derelict);
        spriteBatch = new SpriteBatch();
        InputHandler inputHandler = new InputHandler();
        squidInput = new SquidInput(inputHandler);
        universe = new Universe(spriteBatch, squidInput, inputHandler);
        derelict.setUniverse(universe);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        universe.update();
        universe.render();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        universe.dispose();
        spriteBatch.dispose();
    }
}
