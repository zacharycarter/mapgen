package com.carterza.planet.map.generator;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.carterza.common.CommonRNG;
import com.carterza.planet.map.*;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SquidColorCenter;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.OrderedSet;

import java.util.HashMap;
import java.util.Map;

import static com.carterza.planet.map.generator.MapGenerator.*;

/**
 * Created by zachcarter on 12/15/16.
 */
public class MapTextureGenerator {
    private static SquidColorCenter squidColorCenter = new SquidColorCenter();

    // Heightmap colors
    public static Color DeepColor = new Color(0/255f, 27/255f, 72/255f, 1);
    public static Color MediumColor = new Color(0/255f, 69/255f, 129/255f, 1);
    private static Color ShallowColor = squidColorCenter.light(MediumColor.cpy());
    private static Color CoastalColor = squidColorCenter.light(MediumColor.cpy());
    private static Color FoamColor = new Color(161/255f, 252/255f, 255/255f, 1);

    private static Color IceWater = new Color(210/255f, 255/255f, 252/255f, 1);
    private static Color ColdWater = MediumColor.cpy();
    private static Color RiverWater = ShallowColor.cpy();

    private static Color RiverColor = new Color(30/255f, 120/255f, 200/255f, 1);
    private static Color SandColor = new Color(240 / 255f, 240 / 255f, 64 / 255f, 1);
    private static Color GrassColor = new Color(50 / 255f, 220 / 255f, 20 / 255f, 1);
    private static Color ForestColor = new Color(16 / 255f, 160 / 255f, 0, 1);
    private static Color RockColor = new Color(0.5f, 0.5f, 0.5f, 1);
    private static Color SnowColor = new Color(1, 1, 1, 1);

    // Heat map colors
    private static Color Coldest = new Color(0, 1, 1, 1);
    private static Color Colder = new Color(170/255f, 1, 1, 1);
    private static Color Cold = new Color(0, 229/255f, 133/255f, 1);
    private static Color Warm = new Color(1, 1, 100/255f, 1);
    private static Color Warmer = new Color(1, 100/255f, 0, 1);
    private static Color Warmest = new Color(241/255f, 12/255f, 0, 1);

    // Moisture map colors
    private static Color Dryest = new Color(255/255f, 139/255f, 17/255f, 1);
    private static Color Dryer = new Color(245/255f, 245/255f, 23/255f, 1);
    private static Color Dry = new Color(80/255f, 255/255f, 0/255f, 1);
    private static Color Wet = new Color(85/255f, 255/255f, 255/255f, 1);
    private static Color Wetter = new Color(20/255f, 70/255f, 255/255f, 1);
    private static Color Wettest = new Color(0/255f, 0/255f, 100/255f, 1);

    // Biome map colors
    private static Color Ice = Color.WHITE;
    private static Color DarkIce = squidColorCenter.dimmer(Ice.cpy());

    private static Color Desert = new Color(238/255f, 218/255f, 130/255f, 1);
    private static Color DarkDesert = squidColorCenter.dimmer(Desert.cpy());

    private static Color Savanna = new Color(177/255f, 209/255f, 110/255f, 1);
    private static Color DarkSavanna = squidColorCenter.dimmer(Savanna.cpy());

    private static Color TropicalRainforest = new Color(66/255f, 123/255f, 25/255f, 1);
    private static Color DarkTropicalRainforest = squidColorCenter.dimmer(TropicalRainforest.cpy());

    private static Color Tundra = new Color(96/255f, 131/255f, 112/255f, 1);
    private static Color DarkTundra = squidColorCenter.dimmer(Tundra.cpy());

    private static Color TemperateRainforest = new Color(29/255f, 73/255f, 40/255f, 1);
    private static Color DarkTemperateRainforest = squidColorCenter.dimmer(TemperateRainforest.cpy());

    private static Color Grassland = new Color(164/255f, 225/255f, 99/255f, 1);
    private static Color DarkGrassland = squidColorCenter.dimmer(Grassland.cpy());

    private static Color SeasonalForest = new Color(73/255f, 100/255f, 35/255f, 1);
    private static Color DarkSeasonalForest = squidColorCenter.dimmer(SeasonalForest.cpy());

    private static Color BorealForest = new Color(95/255f, 115/255f, 62/255f, 1);
    private static Color DarkBorealForest = squidColorCenter.dimmer(BorealForest.cpy());

    private static Color Woodland = new Color(139/255f, 175/255f, 90/255f, 1);
    private static Color DarkWoodland = squidColorCenter.dimmer(Woodland.cpy());

    // Wind Colors
    private static Color WindWest = new Color(1, 0, 0, 1);
    private static Color WindNorth = new Color(0, 1, 0, 1);
    private static Color WindEast = new Color(0, 0, 1, 1);
    private static Color WindSouth = new Color(1, 1, 0, 1);

    private static Texture texture;
    private static Pixmap pixmap;

    public static Texture generateWaterMapTexture(int width, int height, char[][] waterLandMap) {
        pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        texture = new Texture(width, height, Pixmap.Format.RGBA8888);

        for(int x = 0; x < WIDTH; x++) {
            for(int y = 0; y < HEIGHT; y++) {
                if(waterLandMap[x][y] == '#')
                    pixmap.drawPixel(x, y, Color.rgba8888(DeepColor));
                else
                    pixmap.drawPixel(x, y, Color.rgba8888(GrassColor));
            }
        }

        PixmapIO.writePNG(Gdx.files.getFileHandle("watermap.png", Files.FileType.Local), pixmap);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        texture.draw(pixmap, 0, 0);
        return texture;
    }

    public static Pixmap generateGameMap(int width, int height, Tile[][] tiles, double coldest, double colder, double cold, double warm) {
        pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        // texture = new Texture(width, height, Pixmap.Format.RGBA8888);

        Map<Double, Double> heights = new HashMap<Double, Double>();
        heights.put(0.0, MapGenerator.DeepWater);
        heights.put(MapGenerator.DeepWater, MapGenerator.ShallowWater);
        heights.put(MapGenerator.ShallowWater, MapGenerator.Sand);
        heights.put(MapGenerator.Sand, MapGenerator.Grass);
        heights.put(MapGenerator.Grass, MapGenerator.Forest);
        heights.put(MapGenerator.Forest, MapGenerator.Rock);
        heights.put(MapGenerator.Rock, 1.0);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                BiomeType value = tiles[x][y].biomeType;
                double heightValue = tiles[x][y].heightValue;
                if(tiles[x][y].heightType == HeightType.River)
                    heightValue = tiles[x][y].originalHeightValue;

                double min = Double.MAX_VALUE, max = Double.MIN_VALUE;

                for(Map.Entry<Double, Double> heightRange : heights.entrySet()) {
                    if(heightValue >= heightRange.getKey() && (heightValue <= heightRange.getValue())) {
                        min = heightRange.getKey();
                        max = heightRange.getValue();
                    }
                }


                if(value != null) {

                    switch (value) {
                        case Ice:
                            pixmap.drawPixel(x,y, Color.rgba8888(DarkIce.cpy().lerp(Ice, (float) ((heightValue - min)/(max - min)))));
                            break;
                        case BorealForest:
                            pixmap.drawPixel(x,y, Color.rgba8888(DarkBorealForest.cpy().lerp(BorealForest, (float) ((heightValue - min)/(max - min)))));
                            break;
                        case Desert:
                            pixmap.drawPixel(x,y, Color.rgba8888(DarkDesert.cpy().lerp(Desert, (float) ((heightValue - min)/(max - min)))));
                            break;
                        case Grassland:
                            pixmap.drawPixel(x,y, Color.rgba8888(DarkGrassland.cpy().lerp(Grassland, (float) ((heightValue - min)/(max - min)))));
                            break;
                        case SeasonalForest:
                            pixmap.drawPixel(x,y, Color.rgba8888(DarkSeasonalForest.cpy().lerp(SeasonalForest, (float) ((heightValue - min)/(max - min)))));
                            break;
                        case Tundra:
                            pixmap.drawPixel(x,y, Color.rgba8888(DarkTundra.cpy().lerp(Tundra, (float) ((heightValue - min)/(max - min)))));
                            break;
                        case Savanna:
                            pixmap.drawPixel(x,y, Color.rgba8888(DarkSavanna.cpy().lerp(Savanna, (float) ((heightValue - min)/(max - min)))));
                            break;
                        case TemperateRainforest:
                            pixmap.drawPixel(x,y, Color.rgba8888(DarkTemperateRainforest.cpy().lerp(TemperateRainforest, (float) ((heightValue - min)/(max - min)))));
                            break;
                        case TropicalRainforest:
                            pixmap.drawPixel(x,y, Color.rgba8888(DarkTropicalRainforest.cpy().lerp(TropicalRainforest, (float) ((heightValue - min)/(max - min)))));
                            break;
                        case Woodland:
                            pixmap.drawPixel(x,y, Color.rgba8888(DarkWoodland.cpy().lerp(Woodland, (float) ((heightValue - min)/(max - min)))));
                            break;
                    }
                }

                HeightType heightType = tiles[x][y].heightType;
                // Water tiles
                if (heightType == HeightType.DeepWater) {
                    pixmap.drawPixel(x,y, Color.rgba8888(DeepColor.cpy().lerp(MediumColor, (float) ((heightValue - min)/(max - min)))));
                    /*pixmap.drawPixel(x, y, Color.rgba8888(DeepColor));*/
                }
                else if (heightType == HeightType.MediumWater) {
                    /*pixmap.drawPixel(x, y, Color.rgba8888(ShallowColor));*/
                    pixmap.drawPixel(x,y, Color.rgba8888(MediumColor.cpy().lerp(ShallowColor, (float) ((heightValue - min)/(max - min)))));
                }
                else if (heightType == HeightType.ShallowWater) {
                    /*pixmap.drawPixel(x, y, Color.rgba8888(ShallowColor));*/
                    pixmap.drawPixel(x,y, Color.rgba8888(MediumColor.cpy().lerp(ShallowColor, (float) ((heightValue - min)/(max - min)))));
                }
                else if (heightType == HeightType.CoastalWater) {
                    /*pixmap.drawPixel(x, y, Color.rgba8888(ShallowColor));*/
                    pixmap.drawPixel(x,y, Color.rgba8888(ShallowColor));
                }

                // draw riverPaths
                double oldRange = 0.5;
                if (heightType == HeightType.River) {
                    /*if(heightValue >= Rock)
                        pixmap.drawPixel(x,y, Color.rgba8888(IceWater.cpy().lerp(ColdWater, (float) ((heightValue - Rock)/(1 - Rock)))));
                    else*/
                    pixmap.drawPixel(x,y, Color.rgba8888(ShallowColor.cpy().lerp(CoastalColor, (float) heightValue)));

                }

                /*if(tiles[x][y].heightType == HeightType.DebugSource) {
                    pixmap.setColor(Color.RED);
                    pixmap.fillCircle(x,y,3);
                }

                if(tiles[x][y].heightType == HeightType.DebugDestination) {
                    pixmap.setColor(Color.GREEN);
                    pixmap.fillCircle(x,y,3);
                }

                if(tiles[x][y].heightType == HeightType.DebugCoastline) {
                    pixmap.setColor(Color.YELLOW);
                    pixmap.fillCircle(x,y,1);
                }*/


                // add a outline
                /*if (tiles[x][y].heightType.getNumVal() >= HeightType.Shore.getNumVal() && tiles[x][y].heightType != HeightType.River)
                {
                    if (tiles[x][y].biomeBitmask != 15) {
                        color = pixmap.getPixel(x,y);
                        tgt = 0;
                        *//*pixmap.drawPixel(x, y, ((int)((color >>> 24) * inv + (tgt >>> 24) * degree) << 24) | ((int)((color >>> 16 & 0xff) * inv + (tgt >>> 16 & 0xff) * degree) << 16) | ((int)((color >>> 8 & 0xff) * inv + (tgt >>> 8 & 0xff) * degree) << 8) | 0xff);*//*
                        pixmap.drawPixel(x, y, Color.rgba8888(new Color(pixmap.getPixel(x, y)).lerp(Color.BLACK, 0.35f)));
                    }

                }*/
            }
        }
        // PixmapIO.writePNG(Gdx.files.getFileHandle("biomemap.png", Files.FileType.Local), pixmap);
        return pixmap;
    }

    public static Texture generateBiomeMapTexture(int width, int height, Tile[][] tiles, double coldest, double colder, double cold, double warm) {
        pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        texture = new Texture(width, height, Pixmap.Format.RGBA8888);

        Map<Double, Double> heights = new HashMap<Double, Double>();
        heights.put(0.0, MapGenerator.DeepWater);
        heights.put(MapGenerator.DeepWater, MapGenerator.ShallowWater);
        heights.put(MapGenerator.ShallowWater, MapGenerator.Sand);
        heights.put(MapGenerator.Sand, MapGenerator.Grass);
        heights.put(MapGenerator.Grass, MapGenerator.Forest);
        heights.put(MapGenerator.Forest, MapGenerator.Rock);
        heights.put(MapGenerator.Rock, 1.0);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                BiomeType value = tiles[x][y].biomeType;
                double heightValue = tiles[x][y].heightValue;
                if(tiles[x][y].heightType == HeightType.River)
                    heightValue = tiles[x][y].originalHeightValue;

                double min = Double.MAX_VALUE, max = Double.MIN_VALUE;

                for(Map.Entry<Double, Double> heightRange : heights.entrySet()) {
                    if(heightValue >= heightRange.getKey() && (heightValue <= heightRange.getValue())) {
                        min = heightRange.getKey();
                        max = heightRange.getValue();
                    }
                }


                if(value != null) {

                    switch (value) {
                        case Ice:
                            pixmap.drawPixel(x,y, Color.rgba8888(DarkIce.cpy().lerp(Ice, (float) ((heightValue - min)/(max - min)))));
                            break;
                        case BorealForest:
                            pixmap.drawPixel(x,y, Color.rgba8888(DarkBorealForest.cpy().lerp(BorealForest, (float) ((heightValue - min)/(max - min)))));
                            break;
                        case Desert:
                            pixmap.drawPixel(x,y, Color.rgba8888(DarkDesert.cpy().lerp(Desert, (float) ((heightValue - min)/(max - min)))));
                            break;
                        case Grassland:
                            pixmap.drawPixel(x,y, Color.rgba8888(DarkGrassland.cpy().lerp(Grassland, (float) ((heightValue - min)/(max - min)))));
                            break;
                        case SeasonalForest:
                            pixmap.drawPixel(x,y, Color.rgba8888(DarkSeasonalForest.cpy().lerp(SeasonalForest, (float) ((heightValue - min)/(max - min)))));
                            break;
                        case Tundra:
                            pixmap.drawPixel(x,y, Color.rgba8888(DarkTundra.cpy().lerp(Tundra, (float) ((heightValue - min)/(max - min)))));
                            break;
                        case Savanna:
                            pixmap.drawPixel(x,y, Color.rgba8888(DarkSavanna.cpy().lerp(Savanna, (float) ((heightValue - min)/(max - min)))));
                            break;
                        case TemperateRainforest:
                            pixmap.drawPixel(x,y, Color.rgba8888(DarkTemperateRainforest.cpy().lerp(TemperateRainforest, (float) ((heightValue - min)/(max - min)))));
                            break;
                        case TropicalRainforest:
                            pixmap.drawPixel(x,y, Color.rgba8888(DarkTropicalRainforest.cpy().lerp(TropicalRainforest, (float) ((heightValue - min)/(max - min)))));
                            break;
                        case Woodland:
                            pixmap.drawPixel(x,y, Color.rgba8888(DarkWoodland.cpy().lerp(Woodland, (float) ((heightValue - min)/(max - min)))));
                            break;
                    }
                }

                // Water tiles
                if (tiles[x][y].heightType == HeightType.DeepWater) {
                    pixmap.drawPixel(x,y, Color.rgba8888(DeepColor.cpy().lerp(MediumColor, (float) ((heightValue - min)/(max - min)))));
                    /*pixmap.drawPixel(x, y, Color.rgba8888(DeepColor));*/
                }
                else if (tiles[x][y].heightType == HeightType.MediumWater) {
                    /*pixmap.drawPixel(x, y, Color.rgba8888(ShallowColor));*/
                    pixmap.drawPixel(x,y, Color.rgba8888(MediumColor.cpy().lerp(ShallowColor, (float) ((heightValue - min)/(max - min)))));
                }
                else if (tiles[x][y].heightType == HeightType.ShallowWater) {
                    /*pixmap.drawPixel(x, y, Color.rgba8888(ShallowColor));*/
                    pixmap.drawPixel(x,y, Color.rgba8888(ShallowColor.cpy().lerp(CoastalColor, (float) ((heightValue - min)/(max - min)))));
                }
                else if (tiles[x][y].heightType == HeightType.CoastalWater) {
                    /*pixmap.drawPixel(x, y, Color.rgba8888(ShallowColor));*/
                    pixmap.drawPixel(x,y, Color.rgba8888(CoastalColor.cpy().lerp(FoamColor, (float) ((heightValue - min)/(max - min)))));
                }

                // draw riverPaths
                double oldRange = 0.5;
                if (tiles[x][y].heightType == HeightType.River) {
                    /*if(heightValue >= Rock)
                        pixmap.drawPixel(x,y, Color.rgba8888(IceWater.cpy().lerp(ColdWater, (float) ((heightValue - Rock)/(1 - Rock)))));
                    else*/
                        pixmap.drawPixel(x,y, Color.rgba8888(ShallowColor.cpy().lerp(CoastalColor, (float) heightValue)));

                }

                /*if(tiles[x][y].heightType == HeightType.DebugSource) {
                    pixmap.setColor(Color.RED);
                    pixmap.fillCircle(x,y,3);
                }

                if(tiles[x][y].heightType == HeightType.DebugDestination) {
                    pixmap.setColor(Color.GREEN);
                    pixmap.fillCircle(x,y,3);
                }

                if(tiles[x][y].heightType == HeightType.DebugCoastline) {
                    pixmap.setColor(Color.YELLOW);
                    pixmap.fillCircle(x,y,1);
                }*/


                // add a outline
                /*if (tiles[x][y].heightType.getNumVal() >= HeightType.Shore.getNumVal() && tiles[x][y].heightType != HeightType.River)
                {
                    if (tiles[x][y].biomeBitmask != 15) {
                        color = pixmap.getPixel(x,y);
                        tgt = 0;
                        *//*pixmap.drawPixel(x, y, ((int)((color >>> 24) * inv + (tgt >>> 24) * degree) << 24) | ((int)((color >>> 16 & 0xff) * inv + (tgt >>> 16 & 0xff) * degree) << 16) | ((int)((color >>> 8 & 0xff) * inv + (tgt >>> 8 & 0xff) * degree) << 8) | 0xff);*//*
                        pixmap.drawPixel(x, y, Color.rgba8888(new Color(pixmap.getPixel(x, y)).lerp(Color.BLACK, 0.35f)));
                    }

                }*/
            }
        }
        PixmapIO.writePNG(Gdx.files.getFileHandle("biomemap.png", Files.FileType.Local), pixmap);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        texture.draw(pixmap, 0, 0);
        return texture;
    }

    public static Texture generateMoistureMapTexture(int width, int height, Tile[][] tiles) {
        pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        texture = new Texture(width, height, Pixmap.Format.RGBA8888);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                switch (tiles[x][y].moistureType)
                {
                    case Dryest:
                        pixmap.drawPixel(x, y, Color.rgba8888(Dryest));
                        break;
                    case Dryer:
                        pixmap.drawPixel(x, y, Color.rgba8888(Dryer));
                        break;
                    case Dry:
                        pixmap.drawPixel(x, y, Color.rgba8888(Dry));
                        break;
                    case Wet:
                        pixmap.drawPixel(x, y, Color.rgba8888(Wet));
                        break;
                    case Wetter:
                        pixmap.drawPixel(x, y, Color.rgba8888(Wetter));
                        break;
                    default:
                        pixmap.drawPixel(x, y, Color.rgba8888(Wettest));
                        break;
                }
            }
        }
        PixmapIO.writePNG(Gdx.files.getFileHandle("moisturemap.png", Files.FileType.Local), pixmap);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        texture.draw(pixmap, 0, 0);
        return texture;
    }

    public static Texture generateHeatMapTexture(int width, int height, Tile[][] tiles) {
        pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        texture = new Texture(width, height, Pixmap.Format.RGBA8888);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
               switch (tiles[x][y].heatType)
                {
                    case Coldest:
                        pixmap.drawPixel(x, y, Color.rgba8888(Coldest));
                        break;
                    case Colder:
                        pixmap.drawPixel(x, y, Color.rgba8888(Colder));
                        break;
                    case Cold:
                        pixmap.drawPixel(x, y, Color.rgba8888(Cold));
                        break;
                    case Warm:
                        pixmap.drawPixel(x, y, Color.rgba8888(Warm));
                        break;
                    case Warmer:
                        pixmap.drawPixel(x, y, Color.rgba8888(Warmer));
                        break;
                    case Warmest:
                        pixmap.drawPixel(x, y, Color.rgba8888(Warmest));
                        break;
                }

                /*if (tiles[x][y].heightType.getNumVal() > 2 && tiles[x][y].bitmask != 15)
                    pixmap.drawPixel(x, y, Color.rgba8888(new Color(pixmap.getPixel(x,y)).lerp(Color.BLACK, 0.4f)));*/
            }

        }
        PixmapIO.writePNG(Gdx.files.getFileHandle("heatmap.png", Files.FileType.Local), pixmap);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        texture.draw(pixmap, 0, 0);
        return texture;
    }




    public static Texture generateHeightMapTexture(int width, int height, Tile[][] tiles, boolean generateNormalMap)
    {
        pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        texture = new Texture(width, height, Pixmap.Format.RGBA8888);

        Pixmap noisePixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        Pixmap invertedNoisePixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        Pixmap bwHeightmapPixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                double heightValue = tiles[x][y].heightValue;
                double invertedHeightValue = 1-heightValue;
                noisePixmap.drawPixel(x, y, Color.rgba8888(new Color((float)heightValue, (float)heightValue, (float)heightValue, 1)));
                invertedNoisePixmap.drawPixel(x, y, Color.rgba8888(new Color((float)invertedHeightValue, (float)invertedHeightValue, (float)invertedHeightValue, 1)));

                switch (tiles[x][y].heightType)
                {
                    case DeepWater:
                        pixmap.drawPixel(x, y, Color.rgba8888(DeepColor));
                        bwHeightmapPixmap.drawPixel(x,y, Color.rgba8888(new Color((float)DeepWater, (float)DeepWater, (float)DeepWater, 1)));
                        break;
                    case ShallowWater:
                        pixmap.drawPixel(x, y, Color.rgba8888(ShallowColor));
                        bwHeightmapPixmap.drawPixel(x,y, Color.rgba8888(new Color((float)ShallowWater, (float)ShallowWater, (float)ShallowWater, 1)));
                        break;
                    case MediumWater:
                        pixmap.drawPixel(x, y, Color.rgba8888(MediumColor));
                        bwHeightmapPixmap.drawPixel(x,y, Color.rgba8888(new Color((float)MediumWater, (float)MediumWater, (float)MediumWater, 1)));
                        break;
                    case CoastalWater:
                        pixmap.drawPixel(x, y, Color.rgba8888(CoastalColor));
                        bwHeightmapPixmap.drawPixel(x,y, Color.rgba8888(new Color((float)CoastalWater, (float)CoastalWater, (float)CoastalWater, 1)));
                        break;
                    case Sand:
                        pixmap.drawPixel(x, y, Color.rgba8888(SandColor));
                        bwHeightmapPixmap.drawPixel(x,y, Color.rgba8888(new Color((float)Sand, (float)Sand, (float)Sand, 1)));
                        break;
                    case Grass:
                        pixmap.drawPixel(x, y, Color.rgba8888(GrassColor));
                        bwHeightmapPixmap.drawPixel(x,y, Color.rgba8888(new Color((float)Grass, (float)Grass, (float)Grass, 1)));
                        break;
                    case Forest:
                        pixmap.drawPixel(x, y, Color.rgba8888(ForestColor));
                        bwHeightmapPixmap.drawPixel(x,y, Color.rgba8888(new Color((float)Forest, (float)Forest, (float)Forest, 1)));
                        break;
                    case Rock:
                        pixmap.drawPixel(x, y, Color.rgba8888(RockColor));
                        bwHeightmapPixmap.drawPixel(x,y, Color.rgba8888(new Color((float)Rock, (float)Rock, (float)Rock, 1)));
                        break;
                    case Snow:
                        pixmap.drawPixel(x, y, Color.rgba8888(SnowColor));
                        bwHeightmapPixmap.drawPixel(x,y, Color.rgba8888(new Color(1, 1, 1, 1)));
                        break;
                    case River:
                        pixmap.drawPixel(x, y, Color.rgba8888(RiverColor));
                        break;
                }

                //darken the color if a edge tile
                /*if (tiles[x][y].heightType.getNumVal() > 2 && tiles[x][y].bitmask != 15)
                    pixmap.drawPixel(x,y, Color.rgba8888(new Color(pixmap.getPixel(x,y)).lerp(Color.BLACK, 0.4f)));*/


            }
        }

        PixmapIO.writePNG(Gdx.files.getFileHandle("color_heightmap.png", Files.FileType.Local), pixmap);
        PixmapIO.writePNG(Gdx.files.getFileHandle("heightmap.png", Files.FileType.Local), bwHeightmapPixmap);
        PixmapIO.writePNG(Gdx.files.getFileHandle("noisemap.png", Files.FileType.Local), noisePixmap);
        PixmapIO.writePNG(Gdx.files.getFileHandle("invertedNoiseMap.png", Files.FileType.Local), invertedNoisePixmap);

        // Generate Normal map
        if(generateNormalMap) {
            generateNormalMapTexture(width, height, tiles, 5.0f);
            /*generateNormalMapTexture(width, height, tiles);*/
        }

        bwHeightmapPixmap.dispose();
        noisePixmap.dispose();
        invertedNoisePixmap.dispose();


        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        texture.draw(pixmap, 0, 0);
        return texture;
    }

    private static Texture generateNormalMapTexture(int width, int height, Tile[][] heightmap, float strength) {
        pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        texture = new Texture(width, height, Pixmap.Format.RGBA8888);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // surrounding pixels
                final double topLeft = heightmap[clampTile(x - 1, width-1)][clampTile(y - 1, height-1)].heightValue;
                final double top = heightmap[clampTile(x - 1, width-1)][clampTile(y, height-1)].heightValue;
                final double topRight = heightmap[clampTile(x - 1, width-1)][clampTile(y + 1, height-1)].heightValue;
                final double right = heightmap[clampTile(x, width-1)][clampTile(y + 1, height-1)].heightValue;
                final double bottomRight = heightmap[clampTile(x + 1, width-1)][clampTile(y + 1, height-1)].heightValue;
                final double bottom = heightmap[clampTile(x + 1, width-1)][clampTile(y, height - 1)].heightValue;
                final double bottomLeft = heightmap[clampTile(x + 1, width-1)][clampTile(y - 1, height-1)].heightValue;
                final double left = heightmap[clampTile(x, width-1)][clampTile(y - 1, height-1)].heightValue;


                // their intensities
                /*final double tl = pixelIntensity(topLeft);
                final double t = pixelIntensity(top);
                final double tr = pixelIntensity(topRight);
                final double r = pixelIntensity(right);
                final double br = pixelIntensity(bottomRight);
                final double b = pixelIntensity(bottom);
                final double bl = pixelIntensity(bottomLeft);
                final double l = pixelIntensity(left);*/

                final float dX = (float) ((topRight + 2.0f * right + bottomRight) - (topLeft + 2.0f * left + bottomLeft));
                final float dY = (float) ((bottomLeft + 2.0f * bottom + bottomRight) - (topLeft + 2.0f * top + topRight));
                final float dZ = 1.0f / strength;

                Vector3 normal = new Vector3(dX, dY, dZ).nor();

                normal.x = (((normal.x +1) * 1) / 2) + 0;
                normal.y = (((normal.y +1) * 1) / 2) + 0;
                normal.z = (((normal.z +1) * 1) / 2) + 0;

                pixmap.drawPixel(x, y, Color.rgba8888(new Color(normal.x, normal.y, normal.z, 1)));
            }
        }


        PixmapIO.writePNG(Gdx.files.getFileHandle("normalmap.png", Files.FileType.Local), pixmap);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        texture.draw(pixmap, 0, 0);
        return texture;
    }

    /*private static Texture generateNormalMapTexture(int width, int height, Tile[][] tiles) {
        pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        texture = new Texture(width, height, Pixmap.Format.RGBA8888);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Vector3 normal;

                // Ignore border pixels
                if(
                        x == 0 || y == 0 || x == width-1 || y == height-1) {
                    normal = new Vector3(1,1,1);
                    System.out.print(normal);
                    continue;
                }

                // Sample neighbors
                float hl = tiles[x-1][y].heightValue;
                float hr = tiles[x+1][y].heightValue;
                float ht = tiles[x][y+1].heightValue;
                float hb = tiles[x][y-1].heightValue;

                Vector3 dx = new Vector3(-1.0f, 0.0f, hr-hl);
                Vector3 dy = new Vector3(0.0f, -1.0f, ht - hb);


                // Compute sobel
                normal = dx.crs(dy);
                normal.z *= .5;
                normal = normal.nor();


                normal.add(1f).scl(0.5f);

                pixmap.drawPixel(x, y, Color.rgba8888(new Color(normal.x * 1.25f, normal.y * 1.25f, normal.z, 1)));
            }
        }


        PixmapIO.writePNG(Gdx.files.getFileHandle("normalmap.png", Files.FileType.Local), pixmap);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        texture.draw(pixmap, 0, 0);
        return texture;
    }*/

    public static Texture generatePoliticalMapTexture(int width, int height, OrderedMap<Character, String> atlas, char[][] politicalMap, Tile[][] tiles, double coldest, double colder, double cold) {
        pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        texture = new Texture(width, height, Pixmap.Format.RGBA8888);

        Map<Character,SColor> factionColorMap = new HashMap<>();
        SColor[] factionColors = new SColor[] {
                SColor.RED
                , SColor.BLUE
                , SColor.YELLOW
                , SColor.GREEN
                , SColor.PURPLE
                , SColor.BLACK
                , SColor.CYAN
                , SColor.MAGENTA
                , SColor.PINK
                , SColor.PINK
                , SColor.GRAY
                , SColor.LIME
                , SColor.BROWN
                , SColor.WHITE
                , SColor.MAROON
                , SColor.PEACH
                , SColor.BEIGE
                , SColor.GOLD
                , SColor.PLATINUM
        };
        OrderedSet<SColor> colors = new OrderedSet<>(factionColors);
        colors.shuffle(CommonRNG.getRng());

        for(int i = 0; i < atlas.keySet().size(); i++) {
            char symbol = atlas.keyAt(i);
            if(Character.isLetter(symbol)) {
                factionColorMap.put(symbol, colors.getAt(i));
            }
        }

        Map<Double, Double> heights = new HashMap<Double, Double>();
        heights.put(0.0, MapGenerator.DeepWater);
        heights.put(MapGenerator.DeepWater, MapGenerator.ShallowWater);
        heights.put(MapGenerator.ShallowWater, MapGenerator.Sand);
        heights.put(MapGenerator.Sand, MapGenerator.Grass);
        heights.put(MapGenerator.Grass, MapGenerator.Forest);
        heights.put(MapGenerator.Forest, MapGenerator.Rock);
        heights.put(MapGenerator.Rock, 1.0);


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                BiomeType value = tiles[x][y].biomeType;
                double heightValue = tiles[x][y].heightValue;

                double min = Double.MAX_VALUE, max = Double.MIN_VALUE;

                for (Map.Entry<Double, Double> heightRange : heights.entrySet()) {
                    if (heightValue >= heightRange.getKey() && (heightValue <= heightRange.getValue())) {
                        min = heightRange.getKey();
                        max = heightRange.getValue();
                    }
                }


                if (value != null) {

                    switch (value) {
                        case Ice:
                            pixmap.drawPixel(x, y, Color.rgba8888(DarkIce.cpy().lerp(Ice, (float) ((heightValue - min) / (max - min)))));
                            break;
                        case BorealForest:
                            pixmap.drawPixel(x, y, Color.rgba8888(DarkBorealForest.cpy().lerp(BorealForest, (float) ((heightValue - min) / (max - min)))));
                            break;
                        case Desert:
                            pixmap.drawPixel(x, y, Color.rgba8888(DarkDesert.cpy().lerp(Desert, (float) ((heightValue - min) / (max - min)))));
                            break;
                        case Grassland:
                            pixmap.drawPixel(x, y, Color.rgba8888(DarkGrassland.cpy().lerp(Grassland, (float) ((heightValue - min) / (max - min)))));
                            break;
                        case SeasonalForest:
                            pixmap.drawPixel(x, y, Color.rgba8888(DarkSeasonalForest.cpy().lerp(SeasonalForest, (float) ((heightValue - min) / (max - min)))));
                            break;
                        case Tundra:
                            pixmap.drawPixel(x, y, Color.rgba8888(DarkTundra.cpy().lerp(Tundra, (float) ((heightValue - min) / (max - min)))));
                            break;
                        case Savanna:
                            pixmap.drawPixel(x, y, Color.rgba8888(DarkSavanna.cpy().lerp(Savanna, (float) ((heightValue - min) / (max - min)))));
                            break;
                        case TemperateRainforest:
                            pixmap.drawPixel(x, y, Color.rgba8888(DarkTemperateRainforest.cpy().lerp(TemperateRainforest, (float) ((heightValue - min) / (max - min)))));
                            break;
                        case TropicalRainforest:
                            pixmap.drawPixel(x, y, Color.rgba8888(DarkTropicalRainforest.cpy().lerp(TropicalRainforest, (float) ((heightValue - min) / (max - min)))));
                            break;
                        case Woodland:
                            pixmap.drawPixel(x, y, Color.rgba8888(DarkWoodland.cpy().lerp(Woodland, (float) ((heightValue - min) / (max - min)))));
                            break;
                    }
                }

                // Water tiles
                if (tiles[x][y].heightType == HeightType.DeepWater) {
                    pixmap.drawPixel(x, y, Color.rgba8888(DeepColor.cpy().lerp(MediumColor, (float) ((heightValue - min) / (max - min)))));
                    /*pixmap.drawPixel(x, y, Color.rgba8888(DeepColor));*/
                } else if (tiles[x][y].heightType == HeightType.MediumWater) {
                    /*pixmap.drawPixel(x, y, Color.rgba8888(ShallowColor));*/
                    pixmap.drawPixel(x, y, Color.rgba8888(MediumColor.cpy().lerp(ShallowColor, (float) ((heightValue - min) / (max - min)))));
                } else if (tiles[x][y].heightType == HeightType.ShallowWater) {
                    /*pixmap.drawPixel(x, y, Color.rgba8888(ShallowColor));*/
                    pixmap.drawPixel(x, y, Color.rgba8888(ShallowColor.cpy().lerp(CoastalColor, (float) ((heightValue - min) / (max - min)))));
                } else if (tiles[x][y].heightType == HeightType.CoastalWater) {
                    /*pixmap.drawPixel(x, y, Color.rgba8888(ShallowColor));*/
                    pixmap.drawPixel(x, y, Color.rgba8888(CoastalColor.cpy().lerp(FoamColor, (float) ((heightValue - min) / (max - min)))));
                }

                // draw riverPaths
                if (tiles[x][y].heightType == HeightType.River)
                {
                    double heatValue = tiles[x][y].heatValue;

                    if (tiles[x][y].heatType == HeatType.Coldest)
                        pixmap.drawPixel(x,y, Color.rgba8888(DeepColor.cpy().lerp(MediumColor, (float) ((heatValue) / (coldest)))));
                    else if (tiles[x][y].heatType == HeatType.Colder)
                        pixmap.drawPixel(x,y, Color.rgba8888(MediumColor.cpy().lerp(ShallowColor, (float) ((heatValue - coldest) / (colder - coldest)))));
                    else if (tiles[x][y].heatType == HeatType.Cold)
                        pixmap.drawPixel(x,y, Color.rgba8888(ShallowColor.cpy().lerp(CoastalColor, (float) ((heatValue - colder) / (cold - colder)))));
                    else
                        pixmap.drawPixel(x, y, Color.rgba8888(FoamColor));
                }

                char symbol = politicalMap[x][y];
                if(factionColorMap.get(symbol) != null) {
                    pixmap.drawPixel(x, y, Color.rgba8888(new Color(pixmap.getPixel(x,y)).lerp(factionColorMap.get(symbol), .75f)));
                }
            }
        }

        /*DungeonUtility.debugPrint(politicalMap);
        for (int i = 0; i < factionColorMap.size() + 2; i++) {
            System.out.println("  " + atlas.keyAt(i) + "  :  " + atlas.getAt(i));
        }
        System.out.println();*/

        for (int i = 0; i < atlas.size(); i++) {
            SColor c = factionColorMap.get(atlas.keyAt(i));
            if(c != null)
                System.out.println("  " + atlas.keyAt(i) + "  :  " + c.getName().replace("DB ", "") + "  :  " + atlas.getAt(i));
            else
                System.out.println("  " + atlas.keyAt(i) + "  :  " + atlas.getAt(i));
        }
        System.out.println();

        PixmapIO.writePNG(Gdx.files.getFileHandle("territorymap.png", Files.FileType.Local), pixmap);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        texture.draw(pixmap, 0, 0);
        return texture;
    }

    private static final float getPixelWrap(int x, int y, int width, int height, Pixmap pixmap) {
        if(MathUtils.isPowerOfTwo(width) && MathUtils.isPowerOfTwo(height)) {
            if (x < 0 || x >= width) x = (width + x) & (width - 1);
            if (y < 0 || y >= height) y = (height + y) & (height - 1);
            return pixmap.getPixel(x,y);
        } else {
            if (x < 0 || x >= width || y < 0 || y >= height) {
                return pixmap.getPixel((y + height) % height,(x + width) % width);
            } else {
                return pixmap.getPixel(y,x);
            }
        }
    }

    private static final Vector3 negate(Vector3 v) {
        return v.set(-v.x, -v.y, -v.z);
    }

    public static void dispose() {
        pixmap.dispose();
        if(texture != null) {
            texture.dispose();
        }
    }

    public static Texture getWindMapTexture(int width, int height, Tile[][] tiles, double[][] wind) {
        pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        texture = new Texture(width, height, Pixmap.Format.RGBA8888);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixmap.drawPixel(x, y, Color.rgba8888(windColor(wind[y][x])));
            }
        }

        PixmapIO.writePNG(Gdx.files.getFileHandle("windmap.png", Files.FileType.Local), pixmap);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        texture.draw(pixmap, 0, 0);
        return texture;
    }

    private static Color windColor(double direction) {
        if(direction > 0.75)
            return gradient(direction, 0.75, 1.0, WindWest, WindNorth);
        if(direction > 0.5)
            return gradient(direction, 0.5, 0.75, WindSouth, WindWest);
        if(direction > 0.25)
            return gradient(direction, 0.25, 0.5, WindEast, WindSouth);
        else
            return gradient(direction, 0, 0.25, WindNorth, WindEast);
    }

    private static Color gradient(double value, double low, double high, Color lowColor, Color highColor) {
        if(high == low) return lowColor.cpy();

        double range =  high - low;
        double x = (value - low) / range;
        double ix = 1.0 - x;

        return new Color(
                (float)(lowColor.r * ix + highColor.r  * x)
                , (float)(lowColor.g * ix + highColor.g  * x)
                , (float)(lowColor.b * ix + highColor.b  * x)
                , 1f
        );
    }

    static final int clampTile(int pX, int pMax)
    {
        if (pX > pMax)
        {
            return pMax;
        }
        else if (pX < 0)
        {
            return 0;
        }
        else
        {
            return pX;
        }
    }
}
