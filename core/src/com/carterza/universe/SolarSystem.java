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
import com.carterza.universe.generator.SolarSystemGenerator;
import squidpony.squidgrid.gui.gdx.*;
import squidpony.squidmath.Coord;

import java.util.List;

import static com.carterza.common.Constants.TILE_HEIGHT;
import static com.carterza.common.Constants.TILE_WIDTH;

/**
 * Created by zachcarter on 12/9/16.
 */
public class SolarSystem extends BaseRoom {

    public static final int SOLAR_SYSTEM_WIDTH = 120;
    public static final int SOLAR_SYSTEM_HEIGHT = 40;
    public static final int VIEWPORT_WIDTH = 120;
    public static final int VIEWPORT_HEIGHT = 40;

    public Coord lastPlayerPos;

    char symbol;
    Color fg;
    Color bg;
    private boolean nebula;
    private String description;
    SolarSystemGenerator solarSystemGenerator;
    StarSystem starSystem;
    Galaxy galaxy;
    Universe universe;
    private boolean generated = false;
    Coord position;
    private TextCellFactory textFactory;
    CelestialBody[][] buffer;
    List<Sun> suns;
    List<Planet> planets;


    public SolarSystem() {

    }

    public SolarSystem(StarSystem starSystem, Galaxy galaxy, Universe universe, char symbol, Color fg, Color bg, Coord position) {
        super(universe);
        this.universe = universe;
        this.galaxy = galaxy;
        this.starSystem = starSystem;
        this.symbol = symbol;
        this.fg = fg;
        this.bg = bg;
        this.position = position;
    }

    public boolean isNebula() {
        return nebula;
    }

    public void setNebula(boolean nebula) {
        this.nebula = nebula;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void enter() {
        if(this.symbol != ' ' && this.symbol != '·') {
            System.out.println("Entering solar system!");
            if(!generated) {
                solarSystemGenerator = new SolarSystemGenerator(this, starSystem, galaxy, universe);
                solarSystemGenerator.generate();
                generated = true;
                suns = solarSystemGenerator.getSuns();
                planets = solarSystemGenerator.getPlanets();
                buffer = solarSystemGenerator.getBuffer();
                initialize();
            }
            universe.setCurrentRoom(this);
            starSystem.setLastPlayerPos(universe.getPlayer().getPosition());
            universe.getPlayer().setPosition(Coord.get(SOLAR_SYSTEM_WIDTH/2, SOLAR_SYSTEM_HEIGHT/2));
            actors.add(universe.getPlayer());
            squidMessageBox.appendWrappingMessage("Entered solar system with : " + suns.size() + " sun(s) and : " + planets.size() + " planets.");
        } else {
            System.out.println("Nothing interesting there, just empty space.");
        }
    }

    private void putMap() {
        for (int i = 0; i < SOLAR_SYSTEM_WIDTH; i++) {
            for (int j = 0; j < SOLAR_SYSTEM_HEIGHT; j++) {
                CelestialBody celestialBody = buffer[i][j];
                display.put(i, j, celestialBody.symbol, celestialBody.color, celestialBody.bgColor);
            }
        }
    }

    @Override
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

    private void initialize() {
        textFactory = DefaultResources.getStretchableFont().setSmoothingMultiplier(2f / (INTERNAL_ZOOM + 1f))
                .width(TILE_WIDTH).height(TILE_HEIGHT).initBySize();

        display = new SquidLayers(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, TILE_WIDTH, TILE_HEIGHT, textFactory.copy());

        stage = new Stage(new StretchViewport(VIEWPORT_WIDTH * TILE_WIDTH, (VIEWPORT_HEIGHT+10) * TILE_HEIGHT), universe.getSpriteBatch());

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, universe.getSquidInput()));

        textFactory = DefaultResources.getStretchableFont().setSmoothingMultiplier(2f / (INTERNAL_ZOOM + 1f))
                .width(TILE_WIDTH).height(TILE_HEIGHT).initBySize();


        squidMessageBox = new SquidMessageBox(SOLAR_SYSTEM_WIDTH, 10,
                textFactory.copy());


        squidMessageBox.setTextSize(TILE_WIDTH, TILE_HEIGHT + INTERNAL_ZOOM * 2);
        display.setTextSize(TILE_WIDTH, TILE_HEIGHT + INTERNAL_ZOOM * 2);


        squidMessageBox.setBounds((VIEWPORT_WIDTH/2*TILE_WIDTH)-(((VIEWPORT_WIDTH/2)/3)*13),0, VIEWPORT_HEIGHT * TILE_WIDTH, TILE_HEIGHT * 10);

        display.setPosition(0, squidMessageBox.getHeight());

        squidMessageBox.appendWrappingMessage("Use numpad or vi-keys (hjklyubn) to move. Use ? for help, f to change colors, q to quit." +
                " Click the top or bottom border of this box to scroll.");


        stage.addActor(display);
        stage.addActor(squidMessageBox);
    }

    public Coord getPosition() {
        return position;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(0,0,SOLAR_SYSTEM_WIDTH-1, SOLAR_SYSTEM_HEIGHT-1);
    }

    public Color getBg() {
        return bg;
    }

    @Override
    public void exit() {
        universe.setCurrentRoom(starSystem);
        universe.getPlayer().setPosition(starSystem.getLastPlayerPos());
    }

    @Override
    public Room getRoomAt(Coord position) {
        return buffer[position.x][position.y];
    }

    public Coord getLastPlayerPos() {
        return lastPlayerPos;
    }

    public void setLastPlayerPos(Coord lastPlayerPos) {
        this.lastPlayerPos = lastPlayerPos;
    }

    @Override
    public Room neighbors(int offsetX, int offsetY) {
        Coord position = universe.getPlayer().getPosition();
        return buffer[position.x+offsetX][position.y+offsetY];
    }

    @Override
    public void dispose() {
        super.dispose();
        if(textFactory != null)
            textFactory.dispose();
        if(buffer != null) {
            for (int x = 0; x < buffer.length; x++) {
                for (int y = 0; y < buffer[0].length; y++) {
                    buffer[x][y].dispose();
                }
            }
        }
    }
}
