package com.carterza.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.carterza.game.Actor;
import com.carterza.game.Character;
import com.carterza.universe.generator.GalaxyGenerator;
import squidpony.squidgrid.gui.gdx.*;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidmath.Coord;
import squidpony.squidmath.CoordPacker;

import static com.carterza.common.Constants.TILE_HEIGHT;
import static com.carterza.common.Constants.TILE_WIDTH;

public class Galaxy extends BaseRoom {

    public static final int SIZE = 40;
    public static final int VIEWPORT_WIDTH = 120;
    public static final int VIEWPORT_HEIGHT = 40;
    public static final double NEBULA_FADE =  0.333;

    Coord lastPlayerPos;

    private StarSystem[][] buffer;
    private StarSystem currentStarSystem;
    private int[][] map;

    private TextCellFactory textFactory;

    public Galaxy(Universe universe, SpriteBatch spriteBatch, SquidInput squidInput) {
        super(universe);

        this.spriteBatch = spriteBatch;

        textFactory = DefaultResources.getStretchableFont().setSmoothingMultiplier(2f / (INTERNAL_ZOOM + 1f))
                .width(TILE_WIDTH).height(TILE_HEIGHT).initBySize();

        display = new SquidLayers(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, TILE_WIDTH, TILE_HEIGHT, textFactory.copy());

        stage = new Stage(new StretchViewport(VIEWPORT_WIDTH * TILE_WIDTH, (VIEWPORT_HEIGHT+10) * TILE_HEIGHT), this.spriteBatch);

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, squidInput));




        squidMessageBox = new SquidMessageBox(SIZE, 10,
                textFactory.copy());


        squidMessageBox.setTextSize(TILE_WIDTH, TILE_HEIGHT + INTERNAL_ZOOM * 2);
        display.setTextSize(TILE_WIDTH, TILE_HEIGHT + INTERNAL_ZOOM * 2);


        squidMessageBox.setBounds((VIEWPORT_WIDTH/2*TILE_WIDTH)-(((VIEWPORT_WIDTH/2)/3)*13),0, VIEWPORT_HEIGHT * TILE_WIDTH, TILE_HEIGHT * 10);
        squidMessageBox.setOffsets((VIEWPORT_WIDTH/2*TILE_WIDTH)-(((VIEWPORT_WIDTH/2)/3)*13),0);
        display.setPosition((VIEWPORT_WIDTH/2*TILE_WIDTH)-(((VIEWPORT_WIDTH/2)/3)*13), squidMessageBox.getHeight());

        squidMessageBox.appendWrappingMessage("Use numpad or vi-keys (hjklyubn) to move. Use ? for help, f to change colors, q to quit." +
                " Click the top or bottom border of this box to scroll.");


        stage.addActor(display);
        stage.addActor(squidMessageBox);


        GalaxyGenerator galaxyGenerator = new GalaxyGenerator(this, universe);
        galaxyGenerator.generate();
        buffer = galaxyGenerator.getBuffer();
        map = galaxyGenerator.getMap();

        Coord initialStarSystemCoords = new DungeonGenerator().utility.randomCell(CoordPacker.pack(map, 1));
        currentStarSystem = buffer[initialStarSystemCoords.x][initialStarSystemCoords.y];
    }

    public void putMap()
    {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                StarSystem starSystem = buffer[i][j];
                display.put(i, j, starSystem.symbol, starSystem.fg, starSystem.bg);
            }
        }
    }

    public void render() {
        putMap();

        for(Actor actor : actors) {
            if(actor instanceof Character) {
                Character c = (Character)actor;
                display.put(c.getPosition().getX(), c.getPosition().getY(), '@', Color.RED);
            }
        }

        stage.draw();
    }

    @Override
    public void addActor(Actor actor) {
        actors.add(actor);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(0,0,SIZE-1, SIZE-1);
    }

    @Override
    public Room getRoomAt(Coord position) {
        return buffer[position.x][position.y];
    }

    @Override
    public void enter() {

    }

    @Override
    public void exit() {

    }

    public StarSystem[][] getBuffer() {
        return buffer;
    }

    public int[][] getMap() {
        return map;
    }

    public StarSystem getCurrentStarSystem() {
        return currentStarSystem;
    }

    @Override
    public Room neighbors(int offsetX, int offsetY) {
        Coord position = universe.getPlayer().getPosition();
        return buffer[position.x+offsetX][position.y+offsetY];
    }

    public void setLastPlayerPos(Coord lastPlayerPos) {
        this.lastPlayerPos = lastPlayerPos;
    }

    public Coord getLastPlayerPos() {
        return lastPlayerPos;
    }

    @Override
    public void dispose() {
        super.dispose();
        textFactory.dispose();
        for(int x = 0; x < buffer.length; x++) {
            for(int y = 0; y < buffer[0].length; y++) {
                buffer[x][y].dispose();
            }
        }
    }
}
