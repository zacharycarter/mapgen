package com.carterza.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.carterza.game.Actor;
import com.carterza.game.Character;
import com.carterza.planet.map.generator.MapGenerator;
import com.carterza.universe.generator.HeightTexture;
import com.carterza.universe.generator.PlanetGenerator;
import com.carterza.universe.generator.SolarSystemGenerator;
import squidpony.squidgrid.gui.gdx.*;
import squidpony.squidgrid.mapping.SpillWorldMap;
import squidpony.squidmath.Coord;

import java.util.List;
import java.util.Map;

import static com.carterza.common.Constants.TILE_HEIGHT;
import static com.carterza.common.Constants.TILE_WIDTH;

/**
 * Created by zachcarter on 12/10/16.
 */
public class Planet extends CelestialBody {

    public static final int PLANET_WIDTH = 120;
    public static final int PLANET_HEIGHT = 40;
    public static final int VIEWPORT_WIDTH = 120;
    public static final int VIEWPORT_HEIGHT = 40;

    int gasType;
    private boolean generated;
    PlanetGenerator planetGenerator;
    SolarSystem solarSystem;
    StarSystem starSystem;
    Galaxy galaxy;
    Universe universe;
    SolarSystemGenerator.PlanetTypes planetType;
    private TextCellFactory textFactory;
    Thread generatorThread;
    MapGenerator mapGenerator;
    Pixmap planetMap;

    SquidLayers planetStatsDisplay;
    SquidMessageBox planetStats;

    public Coord lastPlayerPos;


    int terrainRotationIndex = 0;
    int atmosphereRotationIndex = 0;
    long lastTerrainRotation = 0;
    long lastAtmosphereRotation = 0;

    public Planet(char symbol, Color color, String description, SolarSystemGenerator.PlanetTypes planetType, SolarSystem solarSystem, StarSystem starSystem, Galaxy galaxy, Universe universe, Coord position) {
        super(symbol, color, Color.CLEAR, description, universe);
        this.planetType = planetType;
        this.solarSystem = solarSystem;
        this.starSystem = starSystem;
        this.galaxy = galaxy;
        this.universe =universe;
        this.position = position;
    }

    public Planet(char symbol, Color color, String description) {
        super(symbol,color,description);
    }

    public void setGasType(int gasType) {
        this.gasType = gasType;
    }

    @Override
    public void enter() {
        System.out.println("Entering planet!");
        solarSystem.setLastPlayerPos(universe.getPlayer().getPosition());
        if(!generated) {
            planetGenerator = new PlanetGenerator(this, solarSystem, starSystem, galaxy, universe);
            planetGenerator.generate();

            generated = true;

            initialize();
            squidMessageBox.appendWrappingMessage("Entered planet.");

            generatorThread = new Thread() {
                public void run() {
                    planetMap = mapGenerator.generateGameMap();
                }
            };
            generatorThread.start();
        }
        universe.setCurrentRoom(this);
        universe.getPlayer().setPosition(Coord.get(PLANET_WIDTH/2, PLANET_HEIGHT/2));
        actors.add(universe.getPlayer());
    }

    private void initialize() {
        stage = new Stage(new StretchViewport(VIEWPORT_WIDTH * TILE_WIDTH, (VIEWPORT_HEIGHT+10) * TILE_HEIGHT), universe.getSpriteBatch());

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, universe.getSquidInput()));

        textFactory = DefaultResources.getStretchableFont().setSmoothingMultiplier(2f / (INTERNAL_ZOOM + 1f))
                .width(TILE_WIDTH).height(TILE_HEIGHT).initBySize();

        display = new SquidLayers(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, TILE_WIDTH, TILE_HEIGHT, textFactory.copy());

        textFactory = DefaultResources.getStretchableFont().setSmoothingMultiplier(2f / (INTERNAL_ZOOM + 1f))
                .width(TILE_WIDTH).height(TILE_HEIGHT).initBySize();

        planetStatsDisplay = new SquidLayers(PLANET_WIDTH/4, 30, TILE_WIDTH, TILE_HEIGHT, textFactory.copy());
        planetStatsDisplay.debug();

        squidMessageBox = new SquidMessageBox(PLANET_WIDTH, 10,
                textFactory.copy());
        squidMessageBox.setTextSize(TILE_WIDTH, TILE_HEIGHT + INTERNAL_ZOOM * 2);
        squidMessageBox.setBounds((VIEWPORT_WIDTH/2*TILE_WIDTH)-(((VIEWPORT_WIDTH/2)/3)*13),0, VIEWPORT_HEIGHT * TILE_WIDTH, TILE_HEIGHT * 10);

        planetStats = new SquidMessageBox(PLANET_WIDTH/4, 30, textFactory.copy());
        planetStats.setBounds(0, 0, (PLANET_WIDTH/4)*TILE_WIDTH,30*TILE_HEIGHT);
        planetStats.setTextSize(TILE_WIDTH, TILE_HEIGHT + INTERNAL_ZOOM * 2);
        planetStats.debug();


        display.setTextSize(TILE_WIDTH, TILE_HEIGHT + INTERNAL_ZOOM * 2);
        display.setPosition(0, squidMessageBox.getHeight());


        planetStatsDisplay.addActor(planetStats);
        planetStatsDisplay.setPosition((VIEWPORT_WIDTH-(VIEWPORT_WIDTH/3)) * TILE_WIDTH, VIEWPORT_HEIGHT /3 * TILE_HEIGHT);

        squidMessageBox.appendWrappingMessage("Use numpad or vi-keys (hjklyubn) to move. Use ? for help, f to change colors, q to quit." +
                " Click the top or bottom border of this box to scroll.");


        stage.addActor(display);
        stage.addActor(squidMessageBox);
        stage.addActor(planetStatsDisplay);

        this.mapGenerator = new MapGenerator(360, 120);
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

    private void putMap() {

        int startingX = (VIEWPORT_WIDTH / 3) - (planetGenerator.getDetailWidth() / 2);
        int startingY = (VIEWPORT_HEIGHT / 2) + (planetGenerator.getDetailWidth() / 2);
        int endingX = startingX + planetGenerator.getDetailWidth();
        int endingY = startingY - planetGenerator.getDetailWidth();

        int diffX = 0 - startingX;
        startingX = Math.max(0,startingX);

        int diffY = Math.abs(VIEWPORT_HEIGHT-1 - startingY);
        startingY = Math.min(VIEWPORT_HEIGHT-1, startingY);

        endingX = Math.min(VIEWPORT_WIDTH, endingX);
        endingY = Math.max(-1, endingY);

        int startMaskX = 0;
        if(startingX == 0) startMaskX += diffX;
        int maskX = startMaskX;

        int startMaskY = 0;
        if(startingY == VIEWPORT_HEIGHT-1) startMaskY += diffY;
        int maskY = startMaskY;

        List<List<Double>> detailCircleMask = planetGenerator.getDetailCircleMask();
        for(int y = startingY; y > endingY; y--) {
            for(int x = startingX; x < endingX; x++) {
                if(detailCircleMask.get(maskX).get(maskY) != 0) {
                    Color c = planetGenerator.blendLayers(
                            maskX
                            , maskY
                            , terrainRotationIndex
                            , atmosphereRotationIndex
                            , planetGenerator.getDetailCircleMask()
                            , planetGenerator.getDetailHeightMap()
                            , planetGenerator.getDetailAtmosphere()
                            , planetGenerator.getDetailWidth()
                    );
                    display.put(x, mirrorYCoordinate(y), ' ', c, c);
                }
                maskX++;
            }
            maskX = startMaskX;
            maskY++;
        }

        long time = System.currentTimeMillis();
        if(time > lastTerrainRotation + 500) {
            lastTerrainRotation = time;
            terrainRotationIndex++;
            if(terrainRotationIndex >= planetGenerator.getDetailHeightmapWidth()) terrainRotationIndex = 0;
        }

        if(time > lastAtmosphereRotation + 100) {
            lastAtmosphereRotation = time;
            atmosphereRotationIndex++;
            if(atmosphereRotationIndex >= planetGenerator.getDetailHeightmapWidth()) atmosphereRotationIndex = 0;
        }
    }

    private int mirrorYCoordinate(int y) {
        return (VIEWPORT_HEIGHT-1 -y);
    }

    @Override
    public void exit() {
        universe.setCurrentRoom(solarSystem);
        universe.getPlayer().setPosition(solarSystem.getLastPlayerPos());
    }

    public Coord getLastPlayerPos() {
        return lastPlayerPos;
    }

    public SolarSystemGenerator.PlanetTypes getPlanetType() {
        return planetType;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(0,0,320,240);
    }

    public void setLastPlayerPos(Coord lastPlayerPos) {
        this.lastPlayerPos = lastPlayerPos;
    }

    @Override
    public Room getRoomAt(Coord position) {
        try {
            generatorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new PlanetDetail(universe, this, planetMap, mapGenerator.getTiles());
    }

    @Override
    public void dispose() {
        super.dispose();
        if(textFactory != null)
            textFactory.dispose();
        if(planetGenerator != null)
            planetGenerator.dispose();
        if(mapGenerator != null)
            mapGenerator.dispose();
    }
}
