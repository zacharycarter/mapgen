package com.carterza.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.carterza.game.Actor;
import com.carterza.game.Character;
import com.carterza.universe.generator.StarSystemGenerator;
import squidpony.squidgrid.gui.gdx.DefaultResources;
import squidpony.squidgrid.gui.gdx.SquidLayers;
import squidpony.squidgrid.gui.gdx.SquidMessageBox;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;

import static com.carterza.common.Constants.TILE_HEIGHT;
import static com.carterza.common.Constants.TILE_WIDTH;

public class StarSystem extends BaseRoom {

    public static final int STARSYSTEM_WIDTH = 120;
    public static final int STARSYSTEM_HEIGHT = 40;
    public static final int VIEWPORT_WIDTH = 120;
    public static final int VIEWPORT_HEIGHT = 40;

    Galaxy galaxy;
    SolarSystem[][] buffer;
    char symbol;
    Coord position;
    Color fg;
    Color bg;
    StarSystemGenerator starSystemGenerator;
    private boolean generated = false;
    private TextCellFactory textFactory;
    Coord lastPlayerPos;

    public StarSystem() {
        super();
    }

    public StarSystem(
            Galaxy galaxy
            , Universe universe
            , char symbol
            , Color fg
            , Color bg
            , Coord position) {
        super(universe);
        this.galaxy = galaxy;
        this.symbol = symbol;
        this.fg = fg;
        this.bg = bg;
        this.position = position;
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

        //stage.getCamera().position.set((universe.getPlayer().getPosition().x) * 24, (SIZE-(universe.getPlayer().getPosition().y)) * 24, 0);
        stage.draw();
    }

    private void putMap() {
        for (int i = 0; i < STARSYSTEM_WIDTH; i++) {
            for (int j = 0; j < STARSYSTEM_HEIGHT; j++) {
                SolarSystem solarSystem = buffer[i][j];
                display.put(i, j, solarSystem.symbol, solarSystem.fg, solarSystem.bg);
            }
        }
    }

    @Override
    public void addActor(Actor actor) {

    }

    @Override
    public Room getRoomAt(Coord position) {
        return buffer[position.x][position.y];
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(0,0,STARSYSTEM_WIDTH-1, STARSYSTEM_HEIGHT-1);
    }

    @Override
    public void enter() {
        if(this.symbol != ' ') {
            if(!generated) {
                starSystemGenerator = new StarSystemGenerator(this, galaxy, universe);
                starSystemGenerator.generate();
                generated = true;
                buffer = starSystemGenerator.getBuffer();
                initialize();
                squidMessageBox.appendWrappingMessage("Entered star system.");
            }
            universe.setCurrentRoom(this);
            galaxy.setLastPlayerPos(universe.getPlayer().getPosition());
            universe.getPlayer().setPosition(Coord.get(STARSYSTEM_WIDTH/2, STARSYSTEM_HEIGHT/2));
            actors.add(universe.getPlayer());
        }
    }

    @Override
    public void exit() {
        universe.setCurrentRoom(galaxy);
        universe.getPlayer().setPosition(galaxy.getLastPlayerPos());
    }

    private void initialize() {
        textFactory = DefaultResources.getStretchableFont().setSmoothingMultiplier(2f / (INTERNAL_ZOOM + 1f))
                .width(TILE_WIDTH).height(TILE_HEIGHT).initBySize();

        display = new SquidLayers(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, TILE_WIDTH, TILE_HEIGHT, textFactory.copy());

        stage = new Stage(new StretchViewport(VIEWPORT_WIDTH * TILE_WIDTH, (VIEWPORT_HEIGHT+10) * TILE_HEIGHT), universe.getSpriteBatch());

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, universe.getSquidInput()));

        textFactory = DefaultResources.getStretchableFont().setSmoothingMultiplier(2f / (INTERNAL_ZOOM + 1f))
                .width(TILE_WIDTH).height(TILE_HEIGHT).initBySize();


        squidMessageBox = new SquidMessageBox(STARSYSTEM_WIDTH, 10,
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

    public char getSymbol() {
        return symbol;
    }

    public Color getFg() {
        return fg;
    }

    public Color getBg() {
        return bg;
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
