package com.carterza.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.carterza.common.CommonRNG;
import com.carterza.planet.map.HeightType;
import com.carterza.planet.map.Tile;
import com.carterza.planet.map.generator.MapTextureGenerator;
import com.carterza.universe.generator.GeneratorUtils;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleFractal;
import squidpony.squidgrid.gui.gdx.*;
import squidpony.squidmath.Coord;

import static com.carterza.common.Constants.TILE_HEIGHT;
import static com.carterza.common.Constants.TILE_WIDTH;

/**
 * Created by zachcarter on 12/10/16.
 */
public class PlanetDetail extends BaseRoom {

    public static final int VIEWPORT_WIDTH = 360;
    public static final int VIEWPORT_HEIGHT = 120;

    Planet planet;
    Tile[][] planetMapTiles;
    Pixmap planetMap;
    private TextCellFactory textFactory;
    ModuleFractal cloudNoise = new ModuleFractal();
    ModuleAutoCorrect cloudAutoCorrection = new ModuleAutoCorrect();
    SquidLayers cloudLayer, cloudLayer2;

    float time;

    public PlanetDetail(Universe universe, Planet planet, Pixmap planetMap, Tile[][] tiles) {
        super(universe);
        this.planet = planet;
        this.planetMap = planetMap;
        this.planetMapTiles = tiles;
    }

    @Override
    public void enter() {
        // if(this.symbol != ' ') {
            // if(!generated) {
                // starSystemGenerator = new StarSystemGenerator(this, galaxy, universe);
                // starSystemGenerator.generate();
                // generated = true;
                // buffer = starSystemGenerator.getBuffer();

                time = 0;

                cloudNoise.setNumOctaves(4);
                cloudNoise.setSeed(CommonRNG.getRng().nextLong());
                cloudNoise.setType(ModuleFractal.FractalType.BILLOW);
                cloudNoise.setFrequency(.05);

                cloudAutoCorrection.setSource(cloudNoise);
                cloudAutoCorrection.setSamples(1000);
                cloudAutoCorrection.setLow(0);
                cloudAutoCorrection.setHigh(1);
                cloudAutoCorrection.calculate();

                initialize();
                // squidMessageBox.appendWrappingMessage("Entered star system.");
            // }
            universe.setCurrentRoom(this);
            planet.setLastPlayerPos(universe.getPlayer().getPosition());
            universe.getPlayer().setPosition(Coord.get(VIEWPORT_WIDTH/2, VIEWPORT_HEIGHT/2));
            actors.add(universe.getPlayer());
        // }
    }

    private void initialize() {
        textFactory = DefaultResources.getStretchableSquareFont().setSmoothingMultiplier(2f / (INTERNAL_ZOOM + 1f))
                .width(TILE_WIDTH).height(TILE_HEIGHT).initBySize();

        display = new SquidLayers(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, TILE_WIDTH, TILE_HEIGHT, textFactory.copy());
        cloudLayer = new SquidLayers(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, TILE_WIDTH, TILE_HEIGHT, textFactory.copy());
        cloudLayer2 = new SquidLayers(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, TILE_WIDTH, TILE_HEIGHT, textFactory.copy());

        stage = new Stage(new StretchViewport(VIEWPORT_WIDTH * TILE_WIDTH, (VIEWPORT_HEIGHT+10) * TILE_HEIGHT), universe.getSpriteBatch());

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, universe.getSquidInput()));

        textFactory = DefaultResources.getStretchableFont().setSmoothingMultiplier(2f / (INTERNAL_ZOOM + 1f))
                .width(TILE_WIDTH*3).height(TILE_HEIGHT*3).initBySize();

        squidMessageBox = new SquidMessageBox((planetMap.getWidth() / 3), 10,
                textFactory.copy());


        /*squidMessageBox.setTextSize(TILE_WIDTH, TILE_HEIGHT + INTERNAL_ZOOM * 2);
        display.setTextSize(planetMap.getWidth()/2, planetMap.getHeight()/2);*/


        squidMessageBox.setBounds(0,0, (VIEWPORT_WIDTH * TILE_WIDTH) - TILE_WIDTH*2, TILE_HEIGHT * 30);
        // squidMessageBox.setPosition(TILE_WIDTH, TILE_HEIGHT);
        display.setPosition(0, squidMessageBox.getHeight() + TILE_HEIGHT);
        cloudLayer.setPosition(0, squidMessageBox.getHeight() + TILE_HEIGHT);
        cloudLayer2.setPosition(0, squidMessageBox.getHeight() + TILE_HEIGHT);

        squidMessageBox.appendWrappingMessage("Use numpad or vi-keys (hjklyubn) to move. Use ? for help, f to change colors, q to quit." +
                " Click the top or bottom border of this box to scroll.");


        stage.addActor(display);
        stage.addActor(cloudLayer2);
        stage.addActor(cloudLayer);
        stage.addActor(squidMessageBox);
    }

    @Override
    public void render() {
        time += .15;

        putMap();

       /* for(Actor actor : actors) {
            if(actor instanceof Character) {
                Character c = (Character)actor;
                display.put(c.getPosition().getX(), c.getPosition().getY(), '@', Color.RED);
            }
        }*/

        //stage.getCamera().position.set((universe.getPlayer().getPosition().x) * 24, (SIZE-(universe.getPlayer().getPosition().y)) * 24, 0);
        stage.draw();
    }

    @Override
    public void exit() {
        universe.setCurrentRoom(planet);
        universe.getPlayer().setPosition(planet.getLastPlayerPos());
    }

    private void putMap() {

        for (int i = 0; i < planetMap.getWidth(); i++) {
            for (int j = 0; j < planetMap.getHeight(); j++) {
                Color pixelColor = new Color(planetMap.getPixel(i,j));
                HeightType heightType = planetMapTiles[i][j].heightType;
                if(heightType == HeightType.DeepWater || heightType == HeightType.MediumWater || heightType == HeightType.ShallowWater || heightType == HeightType.CoastalWater) {
                    display.put(i, j, ' ', null, new Color(
                            GeneratorUtils.clampColor((int) ((pixelColor.r*255) + CommonRNG.getRng().between(-7,7)))/255.0f
                            , GeneratorUtils.clampColor((int) ((pixelColor.g*255) + CommonRNG.getRng().between(-7,7)))/255.0f
                            , GeneratorUtils.clampColor((int) ((pixelColor.b*255) + CommonRNG.getRng().between(-7,7)))/255.0f
                            , 1
                    ));
                }  else {
                    display.put(i, j, ' ', null, pixelColor);
                }
                cloudLayer.put(i,j, ' ', null, new Color(1,1,1, (float) cloudNoise.get(i+time,j+time)));
                cloudLayer2.put(i,j, ' ', null, new Color(1,1,1, (float) cloudNoise.get((i*.05)+(time*.05),(j*.05)+(time*.05)) * .75f));
            }
        }


    }

    @Override
    public void dispose() {
        super.dispose();

        cloudNoise = null;
        cloudAutoCorrection = null;

        if(textFactory != null)
            textFactory.dispose();
    }
}
